package fr.fiona.advanced_warp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import fr.fiona.advanced_warp.Advanced_warp;
import fr.fiona.advanced_warp.conversation.confirmchange;
import fr.fiona.advanced_warp.conversation.confirmcreate;
import fr.fiona.advanced_warp.utils.Warp;
import fr.fiona.advanced_warp.utils.Warputils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@CommandAlias("swwarp|warp|advancedwarp")
public class WarpCommand extends BaseCommand {

    private GuiItem add;
    ConversationFactory factory;

    Advanced_warp advancedWarp;

    public  WarpCommand(Advanced_warp advanced_warp){
        advancedWarp = advanced_warp;
        factory = new ConversationFactory(advanced_warp);
    }

    @Default
    @Syntax("[warp]")
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
              Bukkit.getConsoleSender().sendMessage(Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix") +String.format(Advanced_warp.getInstance().language.getLanguageConfig().getString("teleportation-console-message"),p.getName(),w.getName()));
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
              p.sendTitle( Advanced_warp.getInstance().language.getLanguageConfig().getString("welcome-title"), String.format( Advanced_warp.getInstance().language.getLanguageConfig().getString("welcome-subtitle"),w.getOwner().getName()), 1, 30, 1);
          }

      }

    }
    @Subcommand("blacklist")
    @Syntax("[warp]")
    @CommandCompletion("@players")
    @CommandPermission("advancedwarp.blacklist")
    public void Blacklist_warp(CommandSender sender,String nomwarp,String name){
        if (name.equalsIgnoreCase("list"))
        {
            PaginatedGui gui = new PaginatedGui(6, Advanced_warp.getInstance().language.getLanguageConfig().getString("menu-warp-name") );
            gui.setItem(6, 3, ItemBuilder.from(Material.PAPER).setName(ChatColor.GREEN+Advanced_warp.getInstance().language.getLanguageConfig().getString("previous")).asGuiItem(event -> gui.previous()));

            gui.setItem(6, 1, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());
            gui.setItem(6, 2, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());
            gui.setItem(6, 5, ItemBuilder.from(Material.NETHER_STAR).setName(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("name-howtodelete-blacklist")).setLore(ChatColor.AQUA+" ",ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("message-delete-blacklist")).asGuiItem());
            gui.setItem(6, 4, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());
            gui.setItem(6, 6, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());
            gui.setItem(6, 8, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());
            gui.setItem(6, 9, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());

            gui.setItem(6, 7, ItemBuilder.from(Material.PAPER).setName(ChatColor.GREEN+Advanced_warp.getInstance().language.getLanguageConfig().getString("next")).asGuiItem(event -> gui.next()));
            Player p = (Player) sender;
            for (Warp w : Warputils.warps) {
                if ((w.getOwner() == p) && (w.getName().equalsIgnoreCase(nomwarp))) {

                    List<String> black_list = w.getBlackList();
                    for (int i = 0; i < black_list.size(); i++) {


                        int finalI = i;
                        add = ItemBuilder.from(Material.PAPER).setName(ChatColor.BLUE +  black_list.get(finalI) ).setLore(ChatColor.AQUA + Advanced_warp.getInstance().language.getLanguageConfig().getString("message-delete-blacklist")).asGuiItem(event -> {
                            w.addBlackList((Player) sender, black_list.get(finalI));
                        });
                        gui.addItem(add);


                    }
                }
            }
            gui.setDefaultClickAction(event -> {
                    event.setCancelled(true);
                });
            gui.open(p);


        }
        else {
            Player p = (Player) sender;
            for (Warp w : Warputils.warps) {
                if ((w.getOwner() == p) && (w.getName().equalsIgnoreCase(nomwarp))) {
                    w.addBlackList((Player) sender, name);
                    return;
                }
            }
            sender.sendMessage(Advanced_warp.getInstance().language.getLanguageConfig().getString("error-blacklist-player"));
        }

    }

    @Subcommand("change")
    @Syntax("[warp]")
    @CommandPermission("advancedwarp.changewarp")
    public void Change_warp(CommandSender sender,String nomwarp){
        Player p = (Player)sender;
        for(Warp w:Warputils.warps){
            if((w.getOwner() == p) && ( w.getName().equalsIgnoreCase(nomwarp)))
            {
                factory = factory
                        .withFirstPrompt(new confirmchange(p,nomwarp))
                        .withEscapeSequence("/exit")
                        .withTimeout(10)
                        .thatExcludesNonPlayersWithMessage("Go away evil console!");
                factory.buildConversation((Conversable) p).begin();

                return;
            }
        }


    }

    @Subcommand("list|liste")
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
                p.sendTitle( Advanced_warp.getInstance().language.getLanguageConfig().getString("welcome-title"), String.format( Advanced_warp.getInstance().language.getLanguageConfig().getString("welcome-subtitle"),w.getOwner().getName()), 1, 30, 1);

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
            Integer maxwarp = -1;

            for (PermissionAttachmentInfo permission : p.getEffectivePermissions()) {
                if ((permission.getPermission().startsWith("advancedwarp.maxwarp.")) && (permission.getValue())) {
                    maxwarp = Math.max(maxclaim,Integer.parseInt(permission.getPermission().replace("advancedwarp.maxwarp.", "")));

                }
            }
            if(maxclaim == -1)
                maxwarp = Advanced_warp.getInstance().getConfig().getInt("max-warps");
        if (Warputils.getWarps(p.getUniqueId()).size() >= maxwarp)
        {
            sender.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+Advanced_warp.getInstance().language.getLanguageConfig().getString("error-max-warp"));
            return ;
        }

        if (!Pattern.matches(Advanced_warp.getInstance().getConfig().getString("warp-name-regex"), name))
        {
            p.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+Advanced_warp.getInstance().language.getLanguageConfig().getString("error-name"));
            return;
        }

        if(!Advanced_warp.getInstance().getConfig().getStringList("world").contains(p.getWorld().getName()))
        {
           // p.sendMessage(p.getWorld().getName());
            p.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+Advanced_warp.getInstance().language.getLanguageConfig().getString("error-world"));
            return;
        }

        if (Advanced_warp.getEconomy().getBalance((OfflinePlayer)p) < Advanced_warp.getInstance().getConfig().getInt("warp-cost")) {

            p.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+Advanced_warp.getInstance().language.getLanguageConfig().getString("error-money"));
            return;
        }

        Gui gui = new Gui(1, Advanced_warp.getInstance().language.getLanguageConfig().getString("choose-category") );


        gui.setItem(1, 4, ItemBuilder.from(Material.DIRT).setName("BUILD").asGuiItem(event -> {
            factory = factory
                    .withFirstPrompt(new confirmcreate(p,name,"build"))
                    .withEscapeSequence("/exit")
                    .withTimeout(10)
                    .thatExcludesNonPlayersWithMessage("Go away evil console!");
            factory.buildConversation((Conversable) p).begin();
            gui.close(p);
                }));

        gui.setItem(1, 5, ItemBuilder.from(Material.WHEAT).setName("FARM").asGuiItem(event -> {
            factory = factory
                    .withFirstPrompt(new confirmcreate(p,name,"farm"))
                    .withEscapeSequence("/exit")
                    .withTimeout(10)
                    .thatExcludesNonPlayersWithMessage("Go away evil console!");
            factory.buildConversation((Conversable) p).begin();
            gui.close(p);
        }));
        gui.setItem(1, 6, ItemBuilder.from(Material.FISHING_ROD).setName("SHOP").asGuiItem(event -> {
            factory = factory
                    .withFirstPrompt(new confirmcreate(p,name,"shop"))
                    .withEscapeSequence("/exit")
                    .withTimeout(10)
                    .thatExcludesNonPlayersWithMessage("Go away evil console!");
            factory.buildConversation((Conversable) p).begin();
            gui.close(p);
        }));

        gui.setItem(1, 7, ItemBuilder.from(Material.PAPER).setName("AUTRE").asGuiItem(event -> {
            factory = factory
                    .withFirstPrompt(new confirmcreate(p,name,"autre"))
                    .withEscapeSequence("/exit")
                    .withTimeout(10)
                    .thatExcludesNonPlayersWithMessage("Go away evil console!");
            factory.buildConversation((Conversable) p).begin();
            gui.close(p);
        }));

        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });
        gui.open(p);

    }

    @Subcommand("delete")
    @Syntax("[warp]")
    @CommandPermission("advancedwarp.delete")
    public void Delete_Command(CommandSender sender, String nomwarp){

        Player p = (Player) sender;

       ArrayList<Warp> delwarp = Warputils.getWarps(p.getUniqueId());

        for (Warp w : delwarp) {
            if ( w.getName().equalsIgnoreCase(nomwarp) && (w.getOwner() == sender))
            {
                w.delete();
                sender.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+Advanced_warp.getInstance().language.getLanguageConfig().getString("warp-deleted"));
                return;
            }
        }

        sender.sendMessage(ChatColor.RED+Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+Advanced_warp.getInstance().language.getLanguageConfig().getString("error-warp-delete"));

    }




    @Subcommand("info")
    @Syntax("[warp]")
    @CommandPermission("advancedwarp.info")
    public void Info_Command(CommandSender sender, String nomwarp){

        Player p = (Player) sender;

        ArrayList<Warp> delwarp = Warputils.getWarps(p.getUniqueId());

        for (Warp w : delwarp) {
            if ( w.getName().equalsIgnoreCase(nomwarp) && (w.getOwner() == sender))
            {
                PaginatedGui gui = new PaginatedGui(6, Advanced_warp.getInstance().language.getLanguageConfig().getString("menu-warp-name") );
                gui.setItem(6, 3, ItemBuilder.from(Material.PAPER).setName(ChatColor.GREEN+Advanced_warp.getInstance().language.getLanguageConfig().getString("previous")).asGuiItem(event -> gui.previous()));

                gui.setItem(6, 1, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());
                gui.setItem(6, 2, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());
                gui.setItem(6, 5, ItemBuilder.from(Material.NETHER_STAR).setName(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("name-howtocreate-warp")).setLore(ChatColor.AQUA+" ",ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("message-command-createwarp")," ",ChatColor.RED+Advanced_warp.getInstance().language.getLanguageConfig().getString("message-price-createwarp")).asGuiItem());
                gui.setItem(6, 4, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());
                gui.setItem(6, 6, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());
                gui.setItem(6, 8, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());

                gui.setItem(3, 3, ItemBuilder.from(Material.SPAWNER).setName("CHANGER CATEGORIE EN FARM").asGuiItem(event -> {
                    w.setCategory("farm");
                }));
                gui.setItem(3, 4, ItemBuilder.from(Material.TROPICAL_FISH).setName("CHANGER CATEGORIE EN SHOP").asGuiItem(event -> {
                    w.setCategory("shop");
                }));
                gui.setItem(3, 5, ItemBuilder.from(Material.DIAMOND_PICKAXE).setName("CHANGER CATEGORIE EN BUILD").asGuiItem(event -> {
                    w.setCategory("build");
                }));

                gui.setItem(6, 9, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem());

                gui.setItem(6, 7, ItemBuilder.from(Material.PAPER).setName(ChatColor.GREEN+Advanced_warp.getInstance().language.getLanguageConfig().getString("next")).asGuiItem(event -> gui.next()));
                gui.setDefaultClickAction(event -> {
                    event.setCancelled(true);
                });
                gui.open(p);
                return;
            }
        }


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
