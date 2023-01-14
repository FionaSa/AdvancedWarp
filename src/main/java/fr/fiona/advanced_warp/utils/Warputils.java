package fr.fiona.advanced_warp.utils;

import fr.fiona.advanced_warp.Advanced_warp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Warputils {
    public static ArrayList<Warp> warps = new ArrayList<>();

    private static Advanced_warp instance = Advanced_warp.getInstance();

    public static void DeleteWarp(Player p, String name){
        if(warps.contains(name))
            warps.remove(name);
        File warpFile = new File(Advanced_warp.getInstance().getDataFolder() + "/warps", name + ".yml");

        warpFile.delete();

    }

    public static void CreateWarp(Player p, String name, String category){
        File ConfigFile = new File(Advanced_warp.getInstance().getDataFolder() + "/warps", name + ".yml");
        FileConfiguration warp = getWarp(name + ".yml");
        warp.set("location.world", p.getLocation().getWorld().getName());
        warp.set("location.x", Double.valueOf(p.getLocation().getX()));
        warp.set("location.y", Double.valueOf(p.getLocation().getY()));
        warp.set("location.z", Double.valueOf(p.getLocation().getZ()));
        warp.set("location.yaw", Float.valueOf(p.getLocation().getYaw()));
        warp.set("location.pitch", Float.valueOf(p.getLocation().getPitch()));
        warp.set("owner", p.getUniqueId().toString());
        warp.set("time-created", Long.valueOf(System.currentTimeMillis()));
        warp.set("visitors", new ArrayList());
        warp.set("last-visit", Long.valueOf(System.currentTimeMillis()));
        warp.set("countvisitors",0);
        warp.set("blacklist", new ArrayList());
        warp.set("category",category);

        try {
            warp.save(ConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        warps.add(new IWarp(name));

    }

    public static void loadWarps() {
        long start = System.currentTimeMillis();
        int warpcount = 0;
        warps.clear();
        File folder = new File(instance.getDataFolder() + "/warps");
        if (folder.listFiles() == null)
            return;
        for (File file : (File[]) Objects.<File[]>requireNonNull(folder.listFiles())) {
            if (!file.isDirectory() || file.getName().contains(".yml")) {
                warps.add(IWarp.getWarp(file.getName().replace(".yml", "")));
                warpcount++;
            }
        }

       // Bukkit.broadcastMessage("count = "+warpcount);

    }


    static FileConfiguration getWarp(String name) {
        File file = new File(instance.getDataFolder() + "/warps", name);
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        try {
            yamlConfiguration.load(file);
        } catch (IOException|org.bukkit.configuration.InvalidConfigurationException iOException) {}
        return (FileConfiguration)yamlConfiguration;
    }

    public static boolean hasWarp(String name) {
        for (Warp w : warps) {
            if (w.getName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    public static ArrayList<Warp> getWarps(UUID uuid) {
        ArrayList<Warp> warpS = new ArrayList<>();
        for (Warp w : warps) {
            if (w.getOwner().getUniqueId().toString().equalsIgnoreCase(uuid.toString()))
                warpS.add(w);
        }
        return warpS;
    }

    public static ArrayList<OfflinePlayer> getWarpUsers() {
        ArrayList<OfflinePlayer> users = new ArrayList<>();
        for (Warp w : warps) {
            if (users.contains(w.getOwner()))
                continue;
            users.add(w.getOwner());
        }
        return users;
    }

}
