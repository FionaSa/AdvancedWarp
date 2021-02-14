package fr.fiona.advanced_warp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import fr.fiona.advanced_warp.Advanced_warp;
import fr.fiona.advanced_warp.utils.Warp;
import fr.fiona.advanced_warp.utils.Warputils;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import me.mattstudios.mfgui.gui.guis.PaginatedGui;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("warps")
public class WarpsCommand extends BaseCommand{
    private GuiItem add;

    @Default
    public void ListeWarp(CommandSender sender){
        Player p = (Player) sender;
        PaginatedGui gui = new PaginatedGui(6, "Menu de Warp");
        gui.setItem(6, 3, ItemBuilder.from(Material.PAPER).setName(ChatColor.GREEN+"Précédent").asGuiItem(event -> gui.previous()));

        gui.setItem(6, 1, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());
        gui.setItem(6, 2, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());
        gui.setItem(6, 5, ItemBuilder.from(Material.NETHER_STAR).setName(ChatColor.AQUA+"Comment créer un warp ?").setLore(ChatColor.AQUA+" ",ChatColor.AQUA+"/warp create [nom]"," ",ChatColor.RED+"(!) La création coute 5000 éclats d'or").asGuiItem());
        gui.setItem(6, 4, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());
        gui.setItem(6, 6, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());
        gui.setItem(6, 8, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());
        gui.setItem(6, 9, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());

        gui.setItem(6, 7, ItemBuilder.from(Material.PAPER).setName(ChatColor.GREEN+"Suivant").asGuiItem(event -> gui.next()));

        // ArrayList<String> warps = new ArrayList<>();

        //Bukkit.broadcastMessage(String.valueOf(Warputils.warps.size()));

        for (Warp w : Warputils.warps) {
            String visiteur = "Personne";
            String blacklist= ChatColor.AQUA+ "Non";

            // Bukkit.broadcastMessage(String.valueOf(w.getVisitors().size()));
            if(w.getCountVisit()!=0)
                visiteur = w.getVisitors().get(w.getVisitors().size()-1)+ ", "+w.getCountVisit()+" iéme visiteur";

            if(w.getBlackList().contains(p.getName()))
                blacklist = ChatColor.RED +"Oui";




            add = ItemBuilder.from(Material.PAPER).setName(ChatColor.BLUE +w.getName()).setLore(ChatColor.AQUA + "* Position = " + w.getLocation().getBlockX() + "x  " + w.getLocation().getBlockY() + "y  " + w.getLocation().getBlockZ() + "z",ChatColor.AQUA +"* Propriétaire = "+w.getOwner().getName(),ChatColor.AQUA + "* Dernier visiteur : ",ChatColor.BLUE+"   "+visiteur +" ",ChatColor.AQUA + "* Blacklisté : "+blacklist).asGuiItem(event -> {
                if(w.getBlackList().contains(p.getName()))
                {
                    p.sendMessage(ChatColor.AQUA+ Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+Advanced_warp.getInstance().language.getLanguageConfig().getString("blacklist-message"));
                    event.setCancelled(true);
                    return;
                }
                w.updateLastvisit();
                w.addVisitor(p);
                w.updateCountVisit();
                p.teleport(w.getLocation());

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
