package dev.deftu.akuma

public interface CommandContributor<T> {
    public fun register(builder: T)
}
