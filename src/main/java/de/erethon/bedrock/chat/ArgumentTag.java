package de.erethon.bedrock.chat;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.renderer.ComponentRenderer;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://github.com/KyoriPowered/adventure/pull/972/files">https://github.com/KyoriPowered/adventure/pull/972/files</a>
 * @author kezz
 */
public class ArgumentTag implements TagResolver {
    private static final String NAME = "argument";
    private static final String NAME_1 = "arg";

    private final MiniMessageTranslator translator;
    private final Locale locale;
    private final List<? extends ComponentLike> argumentComponents;

    public ArgumentTag(final @NotNull List<? extends ComponentLike> argumentComponents, MiniMessageTranslator translator, Locale locale) {
        this.argumentComponents = Objects.requireNonNull(argumentComponents, "argumentComponents");
        this.translator = Objects.requireNonNull(translator, "translator");
        this.locale = Objects.requireNonNull(locale, "locale");
    }

    @Override
    public @Nullable Tag resolve(final @NotNull String name, final @NotNull ArgumentQueue arguments, final @NotNull Context ctx) throws ParsingException {
        final int index = arguments.popOr("No argument number provided").asInt().orElseThrow(() -> ctx.newException("Invalid argument number", arguments));

        if (index < 0 || index >= argumentComponents.size()) {
            throw ctx.newException("Invalid argument number", arguments);
        }
        ComponentLike componentLike = argumentComponents.get(index).asComponent();
        if (componentLike instanceof TextComponent textComponent) {
            if (textComponent.hoverEvent() != null && textComponent.hoverEvent().action() == HoverEvent.Action.SHOW_TEXT) {
                TranslatableComponent hoverComponent = (TranslatableComponent) textComponent.hoverEvent().value();
                componentLike = textComponent.hoverEvent(HoverEvent.showText(translator.translate(hoverComponent, locale)));
            }
            if (componentLike instanceof TranslatableComponent translatableComponent) {
                componentLike = translator.translate(translatableComponent, locale);
            }
        }
        if (componentLike instanceof TranslatableComponent translatableComponent) {
            componentLike = translator.translate(translatableComponent, locale); // Fix translatable arguments not being translated
        }
        if (componentLike == null) {
            return null;
        }
        return Tag.inserting(componentLike);
    }

    @Override
    public boolean has(final @NotNull String name) {
        return name.equals(NAME) || name.equals(NAME_1);
    }
}