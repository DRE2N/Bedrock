package de.erethon.bedrock.misc;

import net.kyori.adventure.text.Component;

/**
 * @author Fyreum
 */
@FunctionalInterface
public interface ComponentConverter<T> {

    Component convert(T t);

}
