package icu.takeneko.appwebterminal.util

import net.minecraft.world.level.block.state.StateHolder
import net.minecraft.world.level.block.state.properties.Property

operator fun <O, S, T> StateHolder<O, S>.get(property: Property<T>): T where T : Comparable<T> {
    return this.getValue(property)
}

operator fun <O, S, T> StateHolder<O, S>.set(property: Property<T>, value: T): T where T : Comparable<T> {
    this.setValue(property, value)
    return this.getValue(property)
}