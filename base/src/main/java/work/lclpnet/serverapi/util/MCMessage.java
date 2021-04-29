/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * A platform independent Minecraft chat message.
 * An {@link IPlatformBridge} must be implemented for the target platform in order to actually send them.
 * This is needed because, for instance, Bukkit handles chat messages a little different than Forge / Fabric.
 */
public class MCMessage {

    private static String prefix = "LCLPNetwork";

    /**
     * @return Get the prefix for the {@link MCMessage#prefixed()} method.
     */
    public static String getPrefix() {
        return prefix;
    }

    /**
     * Set the prefix for the {@link MCMessage#prefixed()} method.
     * @param prefix The prefix to be used.
     */
    public static void setPrefix(String prefix) {
        MCMessage.prefix = prefix;
    }

    /* */

    protected ColorMode colorMode;
    protected MessageStyle style;
    protected String text = null;
    protected final List<MCMessage> children = new ArrayList<>();

    public MCMessage(MessageColor color, ColorMode colorMode) {
        this(new MessageStyle(color), colorMode);
    }

    public MCMessage(MessageStyle style, ColorMode colorMode) {
        this.style = Objects.requireNonNull(style);
        this.colorMode = Objects.requireNonNull(colorMode);
    }

    /**
     * @return The style of this message
     */
    public MessageStyle getStyle() {
        return style;
    }

    /**
     * @return The {@link ColorMode} of this message, if the value is {@link ColorMode#INHERIT}, the message's parent color is used.
     */
    public ColorMode getColorMode() {
        return colorMode;
    }

    /**
     * @return The child-messages of this message.
     */
    public List<MCMessage> getChildren() {
        return children;
    }

    /**
     * Set the style of this message.
     *
     * @param style The new style.
     * @return The same message.
     */
    public MCMessage setStyle(MessageStyle style) {
        this.style = style;
        return this;
    }

    /**
     * Set the color mode of this message.
     *
     * @see MCMessage#getColorMode() getColorMode() for details.
     * @param mode The {@link ColorMode} to be used.
     * @return The same message.
     */
    public MCMessage setColorMode(ColorMode mode) {
        this.colorMode = mode;
        return this;
    }

    /**
     * Edit the style of this message, while maintaining the "builder-style".
     *
     * @param transformer A function that can be used to transform the style.
     * @return The same message.
     */
    public MCMessage editStyle(Function<MessageStyle, ? extends MessageStyle> transformer) {
        this.style = Objects.requireNonNull(transformer).apply(this.style);
        return this;
    }

    /**
     * Set the color of this message's {@link MessageStyle}.
     *
     * @param color The new color.
     * @return The same message.
     */
    public MCMessage setColor(MessageColor color) {
        this.style = this.style.setColor(color);
        return this;
    }

    /**
     * @return Whether this message is a text node, or if it is a parent node.
     */
    public boolean isTextNode() {
        return text != null;
    }

    /**
     * @return The text of this text node. Or null, if this node is a parent node.
     */
    public String getText() {
        return text;
    }

    /**
     * Set the text of this message.
     *
     * @param text The message.
     * @return The same message.
     */
    public MCMessage text(String text) {
        if(!this.children.isEmpty()) throw new IllegalStateException("A parent node cannot have a text.");
        this.text = Objects.requireNonNull(text);
        return this;
    }

    /**
     * Append a new child message to this message.
     *
     * @param message The message to be appended.
     * @return The same message.
     */
    public MCMessage then(MCMessage message) {
        if(this.text != null) throw new IllegalStateException("A text node cannot have child nodes.");
        this.children.add(Objects.requireNonNull(message));
        return this;
    }

    /**
     * Append a new text message to this message.
     *
     * @param text The text of the child message.
     * @return The same message.
     */
    public MCMessage thenInherit(String text) {
        return then(text, child -> child.setColorMode(ColorMode.INHERIT));
    }

    /**
     * Append a new child message to this message.
     * Using this method, the child message can be transformed as needed.
     *
     * @param text The text of the child message.
     * @param childTransformer A function that transforms the new child message as needed.
     * @return The same message.
     */
    public MCMessage then(String text, Function<MCMessage, ? extends MCMessage> childTransformer) {
        MCMessage child = blank().text(Objects.requireNonNull(text));
        return this.then(childTransformer.apply(child));
    }

    /**
     * Append a dot to the message.
     *
     * @return The same message.
     */
    public MCMessage thenDot() {
        return thenInherit(".");
    }

    /**
     * Append a new translation message to this message.
     *
     * @param translationKey The translation key.
     * @param substitutes Optional substitutes for the translated message.
     * @return The same message.
     */
    public MCMessage thenTranslate(String translationKey, MCMessage... substitutes) {
        return this.then(new MCTranslationMessage(MessageColor.WHITE, ColorMode.INHERIT)
                .setSubstitutes(Arrays.asList(substitutes))
                .text(translationKey)
        );
    }

    @Override
    public String toString() {
        return "MCMessage{" + "colorMode=" + colorMode +
                ", style=" + style +
                ", text='" + text + '\'' +
                ", children=" + children +
                '}';
    }

    /**
     * @return A new blank message.
     */
    public static MCMessage blank() {
        return new MCMessage(MessageColor.WHITE, ColorMode.LOCAL);
    }

    /**
     * @return A new prefixed message.
     */
    public static MCMessage prefixed() {
        return prefixed(MessageColor.BLUE);
    }

    /**
     * @param prefixColor The color of the prefix.
     * @return A new prefixed message.
     */
    public static MCMessage prefixed(MessageColor prefixColor) {
        return blank().then(blank().setColor(prefixColor).text(prefix + "> ")).setColor(MessageColor.GRAY);
    }

    /**
     * @return A new error message with a prefix.
     */
    public static MCMessage error() {
        return prefixed().setColor(MessageColor.RED);
    }

    /**
     * A platform independent translation message.
     */
    public static class MCTranslationMessage extends MCMessage {

        protected List<MCMessage> substitutes = new ArrayList<>();

        public MCTranslationMessage(MessageColor color, ColorMode colorMode) {
            super(color, colorMode);
        }

        public List<MCMessage> getSubstitutes() {
            return substitutes;
        }

        public MCTranslationMessage setSubstitutes(List<MCMessage> substitutes) {
            this.substitutes = substitutes;
            return this;
        }

        @Override
        public String toString() {
            return "MCTranslationMessage{" + "colorMode=" + colorMode +
                    ", style=" + style +
                    ", text='" + text + '\'' +
                    ", children=" + children +
                    ", substitutes=" + substitutes +
                    '}';
        }
    }

    public enum ColorMode {

        INHERIT,
        LOCAL

    }

    public static class MessageStyle {

        private MessageColor color;
        private boolean obfuscated = false, bold = false, strikethrough = false, underline = false, italic = false, reset = false;

        public MessageStyle(MessageColor color) {
            this.color = color;
        }

        public MessageColor getColor() {
            return color;
        }

        public MessageStyle setColor(MessageColor color) {
            this.color = color;
            return this;
        }

        public boolean isObfuscated() {
            return obfuscated;
        }

        public MessageStyle setObfuscated(boolean obfuscated) {
            this.obfuscated = obfuscated;
            return this;
        }

        public boolean isBold() {
            return bold;
        }

        public MessageStyle setBold(boolean bold) {
            this.bold = bold;
            return this;
        }

        public boolean isStrikethrough() {
            return strikethrough;
        }

        public MessageStyle setStrikethrough(boolean strikethrough) {
            this.strikethrough = strikethrough;
            return this;
        }

        public boolean isUnderline() {
            return underline;
        }

        public MessageStyle setUnderline(boolean underline) {
            this.underline = underline;
            return this;
        }

        public boolean isItalic() {
            return italic;
        }

        public MessageStyle setItalic(boolean italic) {
            this.italic = italic;
            return this;
        }

        public boolean isReset() {
            return reset;
        }

        public MessageStyle setReset(boolean reset) {
            this.reset = reset;
            return this;
        }

        @Override
        public String toString() {
            return "MessageStyle{" +
                    "color=" + color +
                    ", obfuscated=" + obfuscated +
                    ", bold=" + bold +
                    ", strikethrough=" + strikethrough +
                    ", underline=" + underline +
                    ", italic=" + italic +
                    ", reset=" + reset +
                    '}';
        }
    }

    public static class MessageColor {

        public static final MessageColor BLACK = new MessageColor(0, 0, 0),
        DARK_BLUE = new MessageColor(0, 0, 170),
        DARK_GREEN = new MessageColor(0, 170, 0),
        DARK_AQUA = new MessageColor(0, 170, 170),
        DARK_RED = new MessageColor(170, 0, 0),
        DARK_PURPLE = new MessageColor(170, 0, 170),
        GOLD = new MessageColor(255, 170, 0),
        GRAY = new MessageColor(170, 170, 170),
        DARK_GRAY = new MessageColor(85, 85, 85),
        BLUE = new MessageColor(85, 85, 255),
        GREEN = new MessageColor(85, 255, 85),
        AQUA = new MessageColor(85, 255, 255),
        RED = new MessageColor(255, 85, 85),
        LIGHT_PURPLE = new MessageColor(255, 855, 255),
        YELLOW = new MessageColor(255, 255, 85),
        WHITE = new MessageColor(255, 255, 255);

        public final int red, green, blue;

        /**
         * @param red   Red component. [0.0;255.0]
         * @param green Green component. [0.0;255.0]
         * @param blue  Blue component. [0.0;255.0]
         */
        public MessageColor(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        @Override
        public String toString() {
            return String.format("rgb(%s; %s; %s)", this.red, this.green, this.blue);
        }
    }

}
