package fr.fiona.advanced_warp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import fr.fiona.advanced_warp.Advanced_warp;
import fr.fiona.advanced_warp.utils.Warp;
import fr.fiona.advanced_warp.utils.Warputils;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import me.mattstudios.mfgui.gui.guis.PaginatedGui;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("warps")
public class WarpsCommand extends BaseCommand{
    private GuiItem add;

    @Default
    public void ListWarp(CommandSender sender) {
        Player p = (Player) sender;

        PaginatedGui gui = new PaginatedGui(6, Advanced_warp.getInstance().language.getLanguageConfig().getString("menu-warp-name") );
        gui.setItem(6, 3, ItemBuilder.from(Material.PAPER).setName(ChatColor.GREEN+Advanced_warp.getInstance().language.getLanguageConfig().getString("previous")).asGuiItem(event -> gui.previous()));

        gui.setItem(6, 1, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());
        gui.setItem(6, 2, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());
        gui.setItem(6, 5, ItemBuilder.from(Material.NETHER_STAR).setName(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("name-howtocreate-warp")).setLore(ChatColor.AQUA+" ",ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("message-command-createwarp")," ",ChatColor.RED+Advanced_warp.getInstance().language.getLanguageConfig().getString("message-price-createwarp")).asGuiItem());
        gui.setItem(6, 4, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());
        gui.setItem(6, 6, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());
        gui.setItem(6, 8, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());
        gui.setItem(6, 9, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());

        gui.setItem(6, 7, ItemBuilder.from(Material.PAPER).setName(ChatColor.GREEN+Advanced_warp.getInstance().language.getLanguageConfig().getString("next")).asGuiItem(event -> gui.next()));

        //ArrayList<String> warps = new ArrayList<>();

        //Bukkit.broadcastMessage(String.valueOf(Warputils.warps.size()));

        for (Warp w : Warputils.warps) {
            String visiteur = Advanced_warp.getInstance().language.getLanguageConfig().getString("nobody");
            String blacklist= ChatColor.AQUA+ Advanced_warp.getInstance().language.getLanguageConfig().getString("no-message");

            // Bukkit.broadcastMessage(String.valueOf(w.getVisitors().size()));
            if(w.getCountVisit()!=0)
                visiteur = w.getVisitors().get(w.getVisitors().size()-1)+ ", "+w.getCountVisit()+Advanced_warp.getInstance().language.getLanguageConfig().getString("count-visitors");

            if(w.getBlackList().contains(p.getName()))
                blacklist = ChatColor.RED +Advanced_warp.getInstance().language.getLanguageConfig().getString("yes-message");




            add = ItemBuilder.from(Material.PAPER).setName(ChatColor.BLUE +w.getName()).setLore(ChatColor.AQUA + Advanced_warp.getInstance().language.getLanguageConfig().getString("message-position") + w.getLocation().getBlockX() + "x  " + w.getLocation().getBlockY() + "y  " + w.getLocation().getBlockZ() + "z",ChatColor.AQUA +"* Propriétaire = "+w.getOwner().getName(),ChatColor.AQUA + Advanced_warp.getInstance().language.getLanguageConfig().getString("message-owner"),ChatColor.BLUE+"   "+visiteur +" ",ChatColor.AQUA +Advanced_warp.getInstance().language.getLanguageConfig().getString("message-blacklist")+blacklist).asGuiItem(event -> {
                if(w.getBlackList().contains(p.getName()))
                {
                    p.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+Advanced_warp.getInstance().language.getLanguageConfig().getString("blacklist-message"));
                    event.setCancelled(true);
                    return;
                }
                w.updateLastvisit();
                w.addVisitor(p);
                w.updateCountVisit();
                if( (!p.hasPermission(Advanced_warp.getInstance().getConfig().getString("permission-bypass-delay") ) && ( Advanced_warp.getInstance().getConfig().getInt("warp-delay") > 0 ))){
                    p.sendMessage(String.format(Advanced_warp.getInstance().language.getLanguageConfig().getString("warp-delay-message"),Advanced_warp.getInstance().getConfig().getInt("warp-delay")));
                    Advanced_warp.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Advanced_warp.getInstance(), new Runnable() { public void run() { p.teleport(w.getLocation()); } }, 20 * Advanced_warp.getInstance().getConfig().getInt("warp-delay")); // 20 (one second in ticks) * 5 (seconds to wait)
                }
                else
                    p.teleport(w.getLocation());
                try {
                    Player owner = Bukkit.getPlayer(w.getOwner().getUniqueId());
                    owner.sendMessage(ChatColor.AQUA + String.format(Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix") + Advanced_warp.getInstance().language.getLanguageConfig().getString("someone-visited"), p.getName()));
                }catch (NullPointerException e) {
                        System.out.print("Player not online");
                    }
                p.sendTitle(ChatColor.AQUA +  Advanced_warp.getInstance().language.getLanguageConfig().getString("welcome-title"), String.format( Advanced_warp.getInstance().language.getLanguageConfig().getString("welcome-subtitle"),w.getOwner().getName()), 1, 30, 1);

            });

            gui.addItem(add);
        }
        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });
        gui.open(p);

        return;
    }

}
