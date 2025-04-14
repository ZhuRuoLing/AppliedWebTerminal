package icu.takeneko.appwebterminal.support.http.routing

import appeng.api.networking.IGrid
import appeng.api.networking.crafting.CalculationStrategy
import appeng.api.networking.crafting.CraftingSubmitErrorCode
import appeng.api.networking.crafting.ICraftingPlan
import appeng.api.networking.crafting.ICraftingSubmitResult
import appeng.api.networking.crafting.UnsuitableCpus
import appeng.api.networking.security.IActionSource
import appeng.api.stacks.AEKeyTypes
import appeng.api.stacks.GenericStack
import appeng.api.storage.AEKeyFilter
import appeng.menu.me.crafting.CraftingPlanSummary
import com.google.common.cache.CacheBuilder
import icu.takeneko.appwebterminal.support.AEKeyObject
import icu.takeneko.appwebterminal.support.AEKeyObject.Companion.serializable
import icu.takeneko.appwebterminal.support.AEKeyTypeObject.Companion.serializable
import icu.takeneko.appwebterminal.support.AENetworkSupport
import icu.takeneko.appwebterminal.support.MECpuStatusBundle
import icu.takeneko.appwebterminal.support.MECpuStatusBundle.Companion.asStatus
import icu.takeneko.appwebterminal.support.MEStack
import icu.takeneko.appwebterminal.support.MEStack.Companion.meStacks
import icu.takeneko.appwebterminal.support.PageMeta
import icu.takeneko.appwebterminal.support.http.plugins.Principal
import icu.takeneko.appwebterminal.support.http.routing.CraftingPlanSubmitErrorCode.Companion.my
import icu.takeneko.appwebterminal.support.http.routing.CraftingPlanSubmitResult.Companion.serializable
import icu.takeneko.appwebterminal.support.http.routing.CraftingPlanSummaryBundle.Companion.serializable
import icu.takeneko.appwebterminal.support.http.routing.MissingIngredientError.Companion.asError
import icu.takeneko.appwebterminal.support.http.routing.UnsuitableCpuError.Companion.serializable
import icu.takeneko.appwebterminal.util.DispatchedSerializer
import icu.takeneko.appwebterminal.util.ResourceLocationSerializer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

private val allCraftingPlans = CacheBuilder.newBuilder()
    .expireAfterAccess(10.minutes.toJavaDuration())
    .build<Int, ICraftingPlan>()

private val idCounter = AtomicInteger()

fun Application.configureAEServiceRouting() {
    routing {
        get("/list") {
            return@get call.respond(AENetworkSupport.listAllTerminals())
        }
        authenticate("jwt") {
            route("/registries") {
                get("/aeKeyTypes") {
                    return@get call.respond(AEKeyTypes.getAll().map { it.serializable() })
                }
            }
            route("/crafting") {
                get("/cpus") {
                    val principal = call.principal<Principal>()!!
                    val grid = AENetworkSupport.getGrid(principal.uuid)
                        ?: return@get call.respond<List<MECpuStatusBundle>>(listOf())
                    return@get call.respond(
                        grid.craftingService.cpus
                            .mapIndexed { index, it -> it.asStatus(index) }
                    )
                }
                get("/craftables") {
                    val principal = call.principal<Principal>()!!
                    val grid = AENetworkSupport.getGrid(principal.uuid)
                        ?: return@get call.respond<List<AEKeyObject>>(listOf())
                    val craftables = grid.craftingService.getCraftables(AEKeyFilter.none())
                        .map { it.serializable() }
                    return@get call.respond(craftables)
                }
                post("/createCraftPlan") {
                    val principal = call.principal<Principal>()!!
                    val request = call.receive<CraftingRequest>()
                    val grid = AENetworkSupport.getGrid(principal.uuid)
                        ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val level = AENetworkSupport.getLevel(principal.uuid)
                        ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val actionHost = AENetworkSupport.getActionHost(principal.uuid)
                        ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val key = grid.craftingService.getCraftables { it.id == request.id && it.type.id == request.type }
                        .firstOrNull()
                        ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val actionSource = IActionSource.ofMachine(actionHost)
                    val craftingPlan = grid.craftingService.beginCraftingCalculation(
                        level,
                        { actionSource },
                        key,
                        request.count,
                        request.calculationStrategy
                    ).get()
                    val summary = craftingPlan.serializable(grid, actionSource)
                    if (!craftingPlan.simulation()) {
                        allCraftingPlans.put(summary.id, craftingPlan)
                    }
                    return@post call.respond(summary)
                }
                post("/submitCraftingPlan") {
                    val principal = call.principal<Principal>()!!
                    val request = call.receive<CraftingPlanSubmitRequest>()
                    val grid = AENetworkSupport.getGrid(principal.uuid)
                        ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val actionHost = AENetworkSupport.getActionHost(principal.uuid)
                        ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val actionSource = IActionSource.ofMachine(actionHost)
                    val craftingPlan = allCraftingPlans.getIfPresent(request.id)
                        ?: return@post call.respond(
                            CraftingPlanSubmitResult(
                                request.id,
                                false,
                                CraftingPlanSubmitErrorCode.CRAFTING_PLAN_NOT_FOUND
                            )
                        )
                    val submitResult = grid.craftingService.submitJob(
                        craftingPlan,
                        null,
                        null,
                        true,
                        actionSource
                    )
                    allCraftingPlans.invalidate(request.id)
                    return@post call.respond(submitResult.serializable(request.id))
                }
            }
            get("/storage") {
                val principal = call.principal<Principal>()!!
                val page = call.queryParameters["page"]?.toIntOrNull() ?: 0
                val limit = call.queryParameters["limit"]?.toIntOrNull() ?: 10
                val grid = AENetworkSupport.getGrid(principal.uuid)
                    ?: return@get call.respond<List<MEStack>>(listOf())
                val meStacks = grid.storageService.cachedInventory.meStacks
                val meStacksChunked = meStacks.chunked(limit)
                val meta = PageMeta(meStacks.size, page, limit, meStacksChunked.size)
                if (page > meStacksChunked.size) return@get call.respond(StorageData(listOf(), meta))
                call.respond(StorageData(meStacksChunked[page], meta))
            }
        }
    }
}

@kotlinx.serialization.Serializable
private data class CraftingRequest(
    @kotlinx.serialization.Serializable(with = ResourceLocationSerializer::class)
    val type: ResourceLocation,
    @kotlinx.serialization.Serializable(with = ResourceLocationSerializer::class)
    val id: ResourceLocation,
    val count: Long,
    val calculationStrategy: CalculationStrategy = CalculationStrategy.REPORT_MISSING_ITEMS
)

@kotlinx.serialization.Serializable
private data class CraftingPlanSubmitRequest(
    val id: Int
)

@kotlinx.serialization.Serializable
private data class CraftingPlanSubmitResult(
    val id: Int,
    val success: Boolean,
    val errorCode: CraftingPlanSubmitErrorCode? = null,
    @kotlinx.serialization.Serializable(with = ErrorDetail.Serializer::class)
    val errorDetail: ErrorDetail? = null
) {
    companion object {
        fun ICraftingSubmitResult.serializable(id: Int) = CraftingPlanSubmitResult(
            id,
            this.successful(),
            this.errorCode()?.my,
            when (this.errorCode()) {
                CraftingSubmitErrorCode.NO_SUITABLE_CPU_FOUND -> (this.errorDetail()!! as UnsuitableCpus).serializable()

                CraftingSubmitErrorCode.MISSING_INGREDIENT -> (this.errorDetail()!! as GenericStack).asError()
                else -> null
            }
        )
    }
}

enum class CraftingPlanSubmitErrorCode {
    INCOMPLETE_PLAN,
    NO_CPU_FOUND,
    NO_SUITABLE_CPU_FOUND,
    CPU_BUSY,
    CPU_OFFLINE,
    CPU_TOO_SMALL,
    MISSING_INGREDIENT,
    CRAFTING_PLAN_NOT_FOUND;

    companion object {
        val CraftingSubmitErrorCode.my: CraftingPlanSubmitErrorCode
            get() = when (this) {
                CraftingSubmitErrorCode.INCOMPLETE_PLAN -> INCOMPLETE_PLAN
                CraftingSubmitErrorCode.NO_CPU_FOUND -> NO_CPU_FOUND
                CraftingSubmitErrorCode.NO_SUITABLE_CPU_FOUND -> NO_SUITABLE_CPU_FOUND
                CraftingSubmitErrorCode.CPU_BUSY -> CPU_BUSY
                CraftingSubmitErrorCode.CPU_OFFLINE -> CPU_OFFLINE
                CraftingSubmitErrorCode.CPU_TOO_SMALL -> CPU_TOO_SMALL
                CraftingSubmitErrorCode.MISSING_INGREDIENT -> MISSING_INGREDIENT
            }
    }
}

private interface ErrorDetail {
    fun type(): String

    companion object Serializer : KSerializer<ErrorDetail> by DispatchedSerializer(
        "type",
        mapOf(
            UnsuitableCpuError.TYPE to UnsuitableCpuError.serializer(),
            MissingIngredientError.TYPE to MissingIngredientError.serializer()
        ),
        String.serializer(),
        ErrorDetail::type
    )
}

@kotlinx.serialization.Serializable
private data class MissingIngredientError(
    val what: AEKeyObject,
    val amount: Long
) : ErrorDetail {
    override fun type(): String {
        return TYPE
    }

    companion object {
        const val TYPE = "missing_ingredient"
        fun GenericStack.asError() = MissingIngredientError(
            this.what.serializable(),
            this.amount
        )
    }
}

@kotlinx.serialization.Serializable
private data class UnsuitableCpuError(
    val offline: Int,
    val busy: Int,
    val tooSmall: Int,
    val excluded: Int
) : ErrorDetail {

    override fun type(): String {
        return TYPE
    }

    companion object {
        const val TYPE = "unsuitable_cpu"
        fun UnsuitableCpus.serializable() = UnsuitableCpuError(
            this.offline,
            this.busy,
            this.tooSmall,
            this.excluded
        )
    }
}


@kotlinx.serialization.Serializable
private data class CraftingPlanSummaryBundle(
    val id: Int,
    val usedBytes: Long,
    val simulation: Boolean,
    val entries: List<CraftingPlanSummaryEntryBundle>
) {
    companion object {
        fun ICraftingPlan.serializable(grid: IGrid, actionSource: IActionSource): CraftingPlanSummaryBundle {
            val summary = CraftingPlanSummary.fromJob(grid, actionSource, this)
            return CraftingPlanSummaryBundle(
                idCounter.incrementAndGet(),
                summary.usedBytes,
                summary.isSimulation,
                summary.entries.map {
                    CraftingPlanSummaryEntryBundle(
                        it.what.serializable(),
                        it.missingAmount,
                        it.storedAmount,
                        it.craftAmount
                    )
                }
            )
        }
    }
}

@kotlinx.serialization.Serializable
private data class CraftingPlanSummaryEntryBundle(
    val what: AEKeyObject,
    var missingAmount: Long,
    var storedAmount: Long,
    var craftAmount: Long
)

@kotlinx.serialization.Serializable
private data class StorageData(val data: List<MEStack>, val meta: PageMeta)
