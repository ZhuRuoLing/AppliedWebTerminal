package icu.takeneko.appwebterminal.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class DispatchedSerializer<T, K>(
    private val keyName: String,
    private val dispatchMap: Map<K, KSerializer<T>>,
    private val keySerializer: KSerializer<K>,
    private val keyGetter: T.() -> K
) : KSerializer<T> {
    private val _descriptor: SerialDescriptor = buildClassSerialDescriptor(
        "Dispatched",
        typeParameters = (dispatchMap.values + keySerializer).map { it.descriptor }.toTypedArray()
    ) {
        this.element(keyName, keySerializer.descriptor)
    }

    override val descriptor: SerialDescriptor
        get() = _descriptor

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): T {
        val composite = decoder.beginStructure(descriptor)
        val keyIdx = composite.decodeElementIndex(descriptor)
        if (keyIdx != 0) {
            throw MissingFieldException(listOf(keyName), "Expected $keyName.")
        }
        val value = composite.decodeSerializableElement(keySerializer.descriptor, keyIdx, keySerializer)
        val serializer =
            dispatchMap[value] ?: throw IllegalArgumentException("No KSerializer found for key type $value")
        return serializer.deserialize(WrappedDecoder(decoder, composite))
    }

    override fun serialize(encoder: Encoder, value: T) {
        val key = value.keyGetter()
        val compositeEncoder = encoder.beginStructure(descriptor)
        compositeEncoder.encodeSerializableElement(descriptor, 0, keySerializer, key)
        val serializer = dispatchMap[key] ?: throw IllegalArgumentException("No KSerializer found for key type $value")
        serializer.serialize(encoder, value)
    }

}

private class WrappedDecoder(decoder: Decoder, val compositeDecoder: CompositeDecoder) : Decoder by decoder {
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return compositeDecoder
    }
}

private class WrappedEncoder(encoder: Encoder, val compositeEncoder: CompositeEncoder) : Encoder by encoder {
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        return compositeEncoder
    }
}