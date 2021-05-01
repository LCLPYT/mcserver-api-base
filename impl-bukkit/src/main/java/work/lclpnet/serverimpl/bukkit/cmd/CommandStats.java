/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import work.lclpnet.lclpnetwork.facade.MCStats;
import work.lclpnet.serverapi.cmd.StatsCommandScheme;
import work.lclpnet.serverapi.translate.MCMessage;
import work.lclpnet.serverapi.translate.ServerTranslation;
import work.lclpnet.serverimpl.bukkit.MCServerBukkit;
import work.lclpnet.serverimpl.bukkit.util.BukkitMCMessageImplementation;
import work.lclpnet.serverimpl.bukkit.util.StatsManager;

import java.text.SimpleDateFormat;
import java.util.*;

import static work.lclpnet.serverimpl.bukkit.util.BukkitServerTranslation.getTranslation;

public class CommandStats extends PlatformCommandSchemeBase implements StatsCommandScheme {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String uuid = player.getUniqueId().toString();
        if(args.length == 0) execute(uuid, new Object[0]);
        else if(args.length == 1) execute(uuid, new Object[] { args[0] });
        else player.sendMessage(MCServerBukkit.pre + ChatColor.RED
                    + getTranslation(player, "stats.usage", getCommandName()));
    }

    @Override
    public void openStats(String invokerUuid, String targetUuid, MCMessage titleMsg, MCStats targetStats) {
        final Player invoker = Bukkit.getPlayer(UUID.fromString(invokerUuid));
        if(invoker == null) throw new NullPointerException("Invoker is null");

        String title = BukkitMCMessageImplementation.convertMCMessageToString(titleMsg, invoker);

        List<MCStats.Entry> entries = new ArrayList<>(targetStats.getStats());
        entries.addAll(targetStats.getStats());
        entries.addAll(targetStats.getStats());
        entries.addAll(targetStats.getStats());
        MCStats.Entry mainEntry = targetStats.getModule("general");
        if(mainEntry != null) entries.remove(mainEntry);

        Inventory inv = createStatsInv(title, mainEntry, entries, 0, invoker, null);

        // Open inventory must be called from the main thread.
        new BukkitRunnable() {
            @Override
            public void run() {
                invoker.openInventory(inv);
            }
        }.runTask(MCServerBukkit.getPlugin());
    }

    public static Inventory createStatsInv(String title, MCStats.Entry mainEntry, List<MCStats.Entry> items, int page, Player viewer, StatsManager.StatsInventory parent) {
        int itemsPerRow = 4;
        int rowsPerPage = 4;
        int rowStartIndex = 1, columnSpacing = 1;

        int rowsRequired = (int) Math.ceil(items.size() / (float) itemsPerRow);
        int pagesRequired = (int) Math.ceil(rowsRequired / (float) rowsPerPage);

        int rows = Math.min(6, Math.max(4, rowsRequired + 2));
        int itemsPerPage = itemsPerRow * (rows - 2);
        int slots = rows * 9;

        final Inventory inv = Bukkit.createInventory(null, slots, ChatColor.stripColor(title));
        final StatsManager.StatsInventory statsInv = new StatsManager.StatsInventory(page, title, mainEntry, items);
        statsInv.setParent(parent);

        ItemStack border = getBorder();

        for (int i = 0; i < 9; i++) inv.setItem(i, border);
        for (int i = slots - 9; i < slots; i++) inv.setItem(i, border);
        for (int i = 9; i < slots - 9; i = (i % 9 == 0 ? i + 8 : i + 1)) inv.setItem(i, border);

        inv.setItem(4, getItem(mainEntry, true, viewer, statsInv));

        int minIdx = itemsPerPage * page;
        int maxIdx = Math.min(minIdx + itemsPerPage, items.size());

        int currentContentRow = 1;
        int currentContentColumn = 0;

        for (int i = minIdx; i < maxIdx; i++) {
            MCStats.Entry entry = items.get(i);

            int rowFirst = currentContentRow * 9;
            int rowColumn = rowStartIndex + currentContentColumn * (1 + columnSpacing);
            inv.setItem(rowFirst + rowColumn, getItem(entry, false, viewer, statsInv));

            if(++currentContentColumn >= 4) {
                currentContentColumn = 0;
                currentContentRow += 1;
            }
        }

        if(pagesRequired > 1) {
            inv.setItem(slots - 5, getPageItem(viewer, page, pagesRequired));

            if(page > 0) {
                ItemStack prevPage = getPrevPageItem(viewer);
                inv.setItem(slots - 9, prevPage);
                statsInv.setPrevPageItem(prevPage);
            }
            if(page < pagesRequired - 1) {
                ItemStack nextPage = getNextPageItem(viewer);
                inv.setItem(slots - 1, nextPage);
                statsInv.setNextPageItem(nextPage);
            }
        }

        if(mainEntry.getType() == MCStats.EntryType.GROUP) {
            ItemStack back = getBackItem(viewer);
            inv.setItem(0, back);
            statsInv.setBackItem(back);
        }

        StatsManager.markAsStats(inv, statsInv);

        return inv;
    }

    private static ItemStack getBackItem(Player viewer) {
        ItemStack is = new ItemStack(Material.ARROW);
        ItemMeta im = is.getItemMeta();
        if(im != null) {
            im.setDisplayName(String.format("%s%s", ChatColor.BLUE, getTranslation(viewer, "stats.back")));
            is.setItemMeta(im);
        }
        return is;
    }

    private static ItemStack getPageItem(Player viewer, int page, int pagesRequired) {
        page += 1;

        ItemStack is = new ItemStack(Material.PAPER);
        if(pagesRequired <= 64) is.setAmount(page);

        ItemMeta im = is.getItemMeta();
        if(im != null) {
            im.setDisplayName(String.format("%s%s", ChatColor.AQUA, getTranslation(viewer, "stats.page.current", page, pagesRequired)));
            is.setItemMeta(im);
        }
        return is;
    }

    private static ItemStack getNextPageItem(Player viewer) {
        ItemStack is = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta im = is.getItemMeta();
        if(im != null) {
            im.setDisplayName(String.format("%s%s", ChatColor.GREEN, getTranslation(viewer, "stats.page.next")));
            is.setItemMeta(im);
        }
        return is;
    }

    private static ItemStack getPrevPageItem(Player viewer) {
        ItemStack is = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta im = is.getItemMeta();
        if(im != null) {
            im.setDisplayName(String.format("%s%s", ChatColor.RED, getTranslation(viewer, "stats.page.prev")));
            is.setItemMeta(im);
        }
        return is;
    }

    private static ItemStack getBorder() {
        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        if(borderMeta != null) {
            borderMeta.setDisplayName(ChatColor.RESET + "");
            border.setItemMeta(borderMeta);
        }
        return border;
    }

    private static ItemStack getItem(MCStats.Entry entry, boolean mainEntry, Player viewer, StatsManager.StatsInventory statsInv) {
        Material mat = Material.BOOK;
        MCStats.Icon icon = entry.getIcon();
        if(icon != null && icon.getMinecraft() != null) {
            String materialKey = icon.getMinecraft();

            for (Material m : Material.values()) {
                NamespacedKey key;
                try {
                    key = m.getKey();
                } catch (Exception e) {
                    continue;
                }

                if(materialKey.equals(key.getKey())) {
                    mat = m;
                    break;
                }
            }
        }

        ItemStack is = new ItemStack(mat);
        ItemMeta im = is.getItemMeta();
        if(im != null) {
            ChatColor displayNameColor;
            if(entry.getType() == MCStats.EntryType.GENERAL) displayNameColor = ChatColor.AQUA;
            else if(entry.getType() == MCStats.EntryType.GROUP) displayNameColor = ChatColor.GREEN;
            else displayNameColor = ChatColor.GOLD;

            im.setDisplayName(String.format("%s%s%s", displayNameColor, ChatColor.BOLD, entry.getTitle()));
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);

            List<String> lore = new ArrayList<>();
            if(entry.getType() == MCStats.EntryType.GROUP) {
                if(!mainEntry)
                    lore.add(String.format("%s%s", ChatColor.YELLOW, getTranslation(viewer, "stats.entry.open_group")));
            } else {
                Map<String, MCStats.Value> properties = entry.getProperties();
                if(properties == null) {
                    lore.add(String.format("%s%s%s", ChatColor.YELLOW, ChatColor.ITALIC, getTranslation(viewer, "stats.entry.none")));
                } else {
                    properties.forEach((key, value) -> {
                        String keyTranslation = getTranslation(viewer, String.format("stat.%s.%s", entry.getName().toLowerCase(Locale.ROOT), key));

                        lore.add(String.format("%s%s: %s%s", ChatColor.GREEN, keyTranslation,
                                ChatColor.YELLOW, getValueAsText(value, viewer)));
                    });
                }
            }

            im.setLore(lore);

            is.setItemMeta(im);
        }

        if(!mainEntry && entry.getType() == MCStats.EntryType.GROUP)
            statsInv.setItemStackGroup(is, entry);

        return is;
    }

    private static String getValueAsText(MCStats.Value value, Player viewer) {
        if(value.getType() == MCStats.ValueType.DATE) {
            Date date = value.getAsDate();
            if(date == null) return getTranslation(viewer, "stats.value.never");

            SimpleDateFormat format = ServerTranslation.getDateFormat(viewer.getLocale());
            return format.format(date);
        } else return value.getValueAsFormattedString();
    }

}
