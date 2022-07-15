package de.erethon.bedrock.misc;

import net.kyori.adventure.text.Component;

/**
 * @since 1.2.1
 * @author Fyreum
 */
@FunctionalInterface
public interface ComponentConverter<T> {

    Component convert(T t);

}
