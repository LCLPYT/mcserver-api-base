/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import work.lclpnet.serverapi.util.MCMessage;

import java.util.*;

import static work.lclpnet.serverapi.util.MCMessage.MessageColor.*;

public class BukkitMCMessageImplementation {

    public static String convertMCMessageToString(MCMessage msg, Player receiver) {
        StringBuilder builder = new StringBuilder();
        recurseMessage(Objects.requireNonNull(msg), null, receiver, builder);
        return builder.toString();
    }

    private static void recurseMessage(MCMessage msg, MCMessage.MessageStyle parentStyle, Player receiver, StringBuilder builder) {
        MCMessage.MessageStyle localStyle;
        switch (msg.getColorMode()) {
            case INHERIT:
                localStyle = parentStyle != null ? parentStyle : msg.getStyle();
                break;
            case LOCAL:
                localStyle = msg.getStyle();
                break;
            default:
                throw new IllegalStateException("Color mode not implemented: " + msg.getColorMode());
        }

        if(msg.isTextNode()) {
            StringBuilder formatBuilder = new StringBuilder();
            getChatColorsFromStyle(localStyle).forEach(formatBuilder::append);
            String format = formatBuilder.toString();

            String text;
            if(msg instanceof MCMessage.MCTranslationMessage) {
                MCMessage.MCTranslationMessage translationMsg = (MCMessage.MCTranslationMessage) msg;

                List<MCMessage> substituteList = translationMsg.getSubstitutes();
                String[] substitutes = new String[substituteList.size()];
                for (int i = 0; i < substituteList.size(); i++) {
                    MCMessage subMsg = substituteList.get(i);
                    substitutes[i] = convertMCMessageToString(subMsg, receiver).concat(format);
                }

                text = ServerTranslations.getTranslation(receiver.getLocale(), translationMsg.getText(), (Object[]) substitutes);
            } else {
                text = msg.getText();
            }

            builder.append(format).append(text);
        } else {
            msg.getChildren().forEach(child -> recurseMessage(child, localStyle, receiver, builder));
        }
    }

    private static final Map<MCMessage.MessageColor, ChatColor> colorMap = new HashMap<>();

    static {
        colorMap.put(BLACK, ChatColor.BLACK);
        colorMap.put(DARK_BLUE, ChatColor.DARK_BLUE);
        colorMap.put(DARK_GREEN, ChatColor.DARK_GREEN);
        colorMap.put(DARK_AQUA, ChatColor.DARK_AQUA);
        colorMap.put(DARK_RED, ChatColor.DARK_RED);
        colorMap.put(DARK_PURPLE, ChatColor.DARK_PURPLE);
        colorMap.put(GOLD, ChatColor.GOLD);
        colorMap.put(GRAY, ChatColor.GRAY);
        colorMap.put(DARK_GRAY, ChatColor.DARK_GRAY);
        colorMap.put(BLUE, ChatColor.BLUE);
        colorMap.put(GREEN, ChatColor.GREEN);
        colorMap.put(AQUA, ChatColor.AQUA);
        colorMap.put(RED, ChatColor.RED);
        colorMap.put(LIGHT_PURPLE, ChatColor.LIGHT_PURPLE);
        colorMap.put(YELLOW, ChatColor.YELLOW);
        colorMap.put(WHITE, ChatColor.WHITE);
    }

    private static List<ChatColor> getChatColorsFromStyle(MCMessage.MessageStyle style) {
        List<ChatColor> list = new ArrayList<>();
        ChatColor color = colorMap.get(style.getColor());
        if(color != null) list.add(color);

        if(style.isObfuscated()) list.add(ChatColor.MAGIC);
        if(style.isBold()) list.add(ChatColor.BOLD);
        if(style.isStrikethrough()) list.add(ChatColor.STRIKETHROUGH);
        if(style.isUnderline()) list.add(ChatColor.UNDERLINE);
        if(style.isItalic()) list.add(ChatColor.ITALIC);
        if(style.isReset()) list.add(ChatColor.RESET);

        return list;
    }

}
