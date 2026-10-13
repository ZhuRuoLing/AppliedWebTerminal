package icu.takeneko.appwebterminal.config;

import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;

public class AppWebTerminalConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue HTTP_PORT;
    public static final ModConfigSpec.ConfigValue<String> FRONTEND_TITLE;
    public static final ModConfigSpec.ConfigValue<String> BACKEND_WEBSOCKET_ENDPOINT;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> NEED_PIN_IN_LANGUAGE;
    public static final ModConfigSpec.EnumValue<MinecraftAssetsApi> ASSETS_SOURCE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        HTTP_PORT = builder.comment("Http Server port for ME Web Terminal.")
            .defineInRange(
                "httpPort",
                11451,
                1,
                65535
            );

        FRONTEND_TITLE = builder.comment("Web Terminal Title")
            .define(
                "frontendTitle",
                "Applied Web Terminal"
            );

        BACKEND_WEBSOCKET_ENDPOINT = builder.comment("Websocket url for frontend to connect", "Use \"~\" for auto detecting")
            .define(
                "backendWebsocketEndpoint",
                "~"
            );

        NEED_PIN_IN_LANGUAGE = builder.comment("Add PinIn support to languages")
            .defineListAllowEmpty(
                "needPinInLanguage",
                List.of("zh_cn", "zh_tw", "zh_hk"),
                () -> "zh_cn",
                value -> value instanceof String
            );

        ASSETS_SOURCE = builder.comment("API used to download Minecraft language files; restart the game to apply changes.")
            .gameRestart()
            .defineEnum("assetsSource", MinecraftAssetsApi.MOJANG);

        SPEC = builder.build();
    }
}
