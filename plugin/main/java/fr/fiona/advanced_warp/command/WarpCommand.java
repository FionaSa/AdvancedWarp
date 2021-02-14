package fr.fiona.advanced_warp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import fr.fiona.advanced_warp.Advanced_warp;
import fr.fiona.advanced_warp.conversation.confirmcreate;
import fr.fiona.advanced_warp.utils.Warp;
import fr.fiona.advanced_warp.utils.Warputils;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import me.mattstudios.mfgui.gui.guis.PaginatedGui;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

@CommandAlias("swwarp")
public class WarpCommand extends BaseCommand {

    private GuiItem  add;
    ConversationFactory factory;

    public WarpCommand(Advanced_warp advanced_warp){
        factory = new ConversationFactory(advanced_warp);
    }

    @Default
    @Syntax("[nomwarp]")
    @CommandCompletion("@nomwarp")
    public void Teleport_warp(CommandSender sender,String name){
        Player p = (Player)sender;
      for(Warp w:Warputils.warps){
          if(w.getName().equalsIgnoreCase(name))
          {
              if(w.getBlackList().contains(p.getName()))
              {
                  p.sendMessage(ChatColor.AQUA + Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix") + Advanced_warp.getInstance().language.getLanguageConfig().getString("blacklist-message"));
                return;
              }

              w.updateLastvisit();
              w.addVisitor(p);
              w.updateCountVisit();
              p.teleport(w.getLocation());
      }

      }

    }
    @Subcommand("blacklist")
    @CommandCompletion("@players")
    public void Blacklist_warp(CommandSender sender,String name){
        Player p = (Player)sender;
        for(Warp w:Warputils.warps){
            if(w.getOwner() == p)
                w.addBlackList((Player)sender,name);
        }

    }

    @Subcommand("list|liste")
    public void ListWarp(CommandSender sender) {
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

       //ArrayList<String> warps = new ArrayList<>();

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
                   p.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+Advanced_warp.getInstance().language.getLanguageConfig().getString("blacklist-message"));
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

    @Subcommand("create")
    @CommandPermission("advancedwarp.create")
    public void Create_Command(CommandSender sender,String name){
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+Advanced_warp.getInstance().language.getLanguageConfig().getString("error-isconsole"));
            return ;
        }

        if(Warputils.hasWarp(name)){
            sender.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+Advanced_warp.getInstance().language.getLanguageConfig().getString("error-warp-already-exist"));
            return;
        }

        Player p = (Player)sender;

        if (!(name.length() >= Advanced_warp.getInstance().getConfig().getInt("min-length") && name.length() <= Advanced_warp.getInstance().getConfig().getInt("max-length")))
        {
            sender.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+Advanced_warp.getInstance().language.getLanguageConfig().getString("error-name-length"));
            return ;
        }

        if (Warputils.getWarps(p.getUniqueId()).size() >= Advanced_warp.getInstance().getConfig().getInt("max-warps"))
        {
            sender.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+Advanced_warp.getInstance().language.getLanguageConfig().getString("error-max-warp"));
            return ;
        }

        if (!Pattern.matches(Advanced_warp.getInstance().getConfig().getString("warp-name-regex"), name))
        {
            p.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+Advanced_warp.getInstance().language.getLanguageConfig().getString("error-name"));
            return;
        }

        if(!(p.getWorld().getName().equalsIgnoreCase("world")|| p.getWorld().getName().equalsIgnoreCase("stratos")))
        {
           // p.sendMessage(p.getWorld().getName());
            p.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+Advanced_warp.getInstance().language.getLanguageConfig().getString("error-world"));
            return;
        }

        if (Advanced_warp.getEconomy().getBalance((OfflinePlayer)p) < Advanced_warp.getInstance().getConfig().getInt("warp-cost")) {

            p.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+Advanced_warp.getInstance().language.getLanguageConfig().getString("error-money"));
            return;
        }



        factory = factory
                .withFirstPrompt(new confirmcreate(p,name))
                .withEscapeSequence("/exit")
                .withTimeout(10)
                .thatExcludesNonPlayersWithMessage("Go away evil console!");
        factory.buildConversation((Conversable) p).begin();




    }

    @Subcommand("delete")
    @CommandPermission("advancedwarp.delete")
    public void Delete_Command(CommandSender sender){

        Player p = (Player) sender;

       ArrayList<Warp> delwarp = Warputils.getWarps(p.getUniqueId());

        for (Warp w : delwarp) {
            w.delete();
        }

        sender.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+Advanced_warp.getInstance().language.getLanguageConfig().getString("warp-deleted"));

    }


  static FileConfiguration getWarp(String name) {
    File file = new File(Advanced_warp.getInstance().getDataFolder() + "/warps", name);
    YamlConfiguration yamlConfiguration = new YamlConfiguration();
    try {
        yamlConfiguration.load(file);
    } catch (IOException | org.bukkit.configuration.InvalidConfigurationException iOException) {}
    return (FileConfiguration)yamlConfiguration;
}

    @HelpCommand
    public void Help_Command(CommandSender sender){

        sender.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("line"));
        sender.sendMessage("");
        sender.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("warp-create-message"));
        sender.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("warp-list-message"));
        sender.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("warp-delete-message"));
        sender.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("warp-tp-message"));
        sender.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("warp-add-blacklist"));
        sender.sendMessage("");
        sender.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("line"));
    }
}