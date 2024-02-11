package de.erethon.bedrock.chat;

import java.text.MessageFormat;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://github.com/KyoriPowered/adventure/pull/972">https://github.com/KyoriPowered/adventure/pull/972</a>
 * @author kezz, Fyreum
 */
public abstract class MiniMessageTranslator implements Translator {

    final MiniMessage mm = MiniMessage.miniMessage();

    protected abstract @Nullable String getMiniMessageString(final @NotNull String key, final @NotNull Locale locale);

    @Override
    public final @Nullable MessageFormat translate(final @NotNull String key, final @NotNull Locale locale) {
        return null;
    }

    @Override
    public @Nullable Component translate(final @NotNull TranslatableComponent component, final @NotNull Locale locale) {
        final String miniMessageString = getMiniMessageString(component.key(), locale);

        if (miniMessageString == null) {
            return null;
        }

        final Component resultingComponent;

        if (component.arguments().isEmpty()) {
            resultingComponent = mm.deserialize(MessageUtil.replaceLegacyChars(miniMessageString));
        } else {
            resultingComponent = mm.deserialize(MessageUtil.replaceLegacyChars(miniMessageString), new ArgumentTag(component.arguments()));
        } if (component.children().isEmpty()) {
            return resultingComponent;
        } else {
            return resultingComponent.children(component.children());
        }
    }

}
