package fr.fiona.advanced_warp;

import co.aikar.commands.*;
import fr.fiona.advanced_warp.command.WarpsCommand;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import fr.fiona.advanced_warp.command.WarpCommand;
import fr.fiona.advanced_warp.config.LanguageConfig;
import fr.fiona.advanced_warp.utils.Warputils;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Advanced_warp extends JavaPlugin {

    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;

    private static Advanced_warp instance;

    public LanguageConfig language = new LanguageConfig();

    public static PaperCommandManager manager;


    public static Advanced_warp getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("    _      ____   __     __     _      _   _      ____ U _____ u ____                         _       ____      ____    ");
        Bukkit.getConsoleSender().sendMessage("U  /\"\\  u |  _\"\\  \\ \\   /\"/uU  /\"\\  u | \\ |\"|  U /\"___|\\| ___\"|/|  _\"\\       __        __ U  /\"\\  uU |  _\"\\ u U|  _\"\\ u ");
        Bukkit.getConsoleSender().sendMessage(" \\/ _ \\/ /| | | |  \\ \\ / //  \\/ _ \\/ <|  \\| |> \\| | u   |  _|\" /| | | |      \\\"\\      /\"/  \\/ _ \\/  \\| |_) |/ \\| |_) |/ ");
        Bukkit.getConsoleSender().sendMessage(" / ___ \\ U| |_| |\\ /\\ V /_,-./ ___ \\ U| |\\  |u  | |/__  | |___ U| |_| |\\     /\\ \\ /\\ / /\\  / ___ \\   |  _ <    |  __/   ");
        Bukkit.getConsoleSender().sendMessage("/_/   \\_\\ |____/ uU  \\_/-(_//_/   \\_\\ |_| \\_|    \\____| |_____| |____/ u    U  \\ V  V /  U/_/   \\_\\  |_| \\_\\   |_|      ");
        Bukkit.getConsoleSender().sendMessage(" \\\\    >>  |||_     //       \\\\    >> ||   \\\\,-._// \\\\  <<   >>  |||_       .-,_\\ /\\ /_,-. \\\\    >>  //   \\\\_  ||>>_    ");
        Bukkit.getConsoleSender().sendMessage("(__)  (__)(__)_)   (__)     (__)  (__)(_\")  (_/(__)(__)(__) (__)(__)_)       \\_)-'  '-(_/ (__)  (__)(__)  (__)(__)__)   ");
        Bukkit.getConsoleSender().sendMessage("");

        Bukkit.getConsoleSender().sendMessage("§b[AdvancedWarp] §2Loading ..");
        instance = this;

        //Charge les warps en mémoire
        Warputils.loadWarps();

        //Charge l'économie
        if (!setupEconomy() ) {
            Bukkit.broadcastMessage("Error, Vault not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //Charge les config
        saveDefaultConfig();
        language.createlanguageConfig();

         manager = new PaperCommandManager((Plugin)this);
        manager.registerCommand((BaseCommand)new WarpCommand(this));
        manager.registerCommand((BaseCommand)new WarpsCommand());

        Bukkit.getConsoleSender().sendMessage("§b[AdvancedWarp] §2Loaded successful ");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }

}
