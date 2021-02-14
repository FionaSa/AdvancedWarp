package fr.fiona.advanced_warp.utils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import fr.fiona.advanced_warp.Advanced_warp;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class IWarp implements Warp {
    private String name;

    private Location loc;

    private Long lastvisit;

    private Long timecreated;

    private int countVisit;

    private List<String> visitors = new ArrayList<>();

    private List<String> blackList = new ArrayList<>();

    private UUID owner;

    private File warpFile;

    private FileConfiguration warpConfig;

    private String texture;



    public List<String> getVisitors() {
        return this.visitors;
    }

    public List<String> getBlackList(){return this.blackList;}

    public Long getLastvisit() {
        return this.lastvisit;
    }

    public Long getTimecreated() {
        return this.timecreated;
    }


    public String getTexture() {
        return this.texture;
    }

    public void setTexture(String value) {
        this.warpConfig.set("texture", value);
        saveConfig();
    }

    public void updateLastvisit() {
        this.warpConfig.set("last-visit", Long.valueOf(System.currentTimeMillis()));
        saveConfig();
    }

    @Override
    public int getCountVisit(){ return countVisit; }

    @Override
    public void updateCountVisit() {
        countVisit ++;
        this.warpConfig.set("countvisitors", countVisit);
        saveConfig();
    }

    public void addVisitor(Player p) {
        //if (!this.visitors.contains(p.getName())) {

            if(this.visitors.isEmpty())
                this.visitors.add(0,p.getName());
            else
                this.visitors.add(p.getName());


            this.warpConfig.set("visitors", this.visitors);

            saveConfig();
      //  }
    }


    public void addBlackList(Player p,String name) {
        if (!this.blackList.contains(name)) {

            if(this.blackList.isEmpty())
                this.blackList.add(0,name);
            else
                this.blackList.add(name);


            List<String> listOfStrings = this.blackList;
            this.warpConfig.set("blacklist", listOfStrings);

            saveConfig();
            p.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+name+Advanced_warp.getInstance().language.getLanguageConfig().getString("blacklist-add"));
        }else
        {
            Iterator<String> itr = this.blackList.iterator();

            while (itr.hasNext())
            {
                String number = itr.next();
                if (number.equals(name))
                {
                 //   Bukkit.broadcastMessage(String.valueOf(this.blackList));
                    itr.remove();

                  //  Bukkit.broadcastMessage(String.valueOf(this.blackList));

                    List<String> listOfStrings = this.blackList;
                    this.warpConfig.set("blacklist", listOfStrings);

                    saveConfig();

                    p.sendMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+name+Advanced_warp.getInstance().language.getLanguageConfig().getString("blacklist-remove"));
                }
            }


        }
    }

    private void saveConfig() {
        try {
            this.warpConfig.save(this.warpFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return this.name;
    }

    public Location getLocation() {
        return this.loc;
    }

    public OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(this.owner);
    }

    IWarp(String name) {
        this.name = name;
        FileConfiguration config = Warputils.getWarp(name + ".yml");
        this.loc = new Location(Bukkit.getWorld(config.getString("location.world")), config.getDouble("location.x"), config.getDouble("location.y"), config.getDouble("location.z"), (float)config.getDouble("location.yaw"), (float)config.getDouble("location.pitch"));
        this.lastvisit = Long.valueOf(config.getLong("last-visit"));
        this.timecreated = Long.valueOf(config.getLong("time-created"));
        this.owner = UUID.fromString(config.getString("owner"));
        this.texture = config.getString("texture");
        this.warpConfig = config;
        this.warpFile = new File(Advanced_warp.getInstance().getDataFolder() + "/warps", name + ".yml");

      //  Bukkit.broadcastMessage("visiteurs = "+config.getStringList("visitors").get(0));

        this.visitors = config.getStringList("visitors");

        this.countVisit = config.getInt("countvisitors");

        this.blackList = config.getStringList("blacklist");

    }

    public static Warp getWarp(String name) {
        return new IWarp(name);
    }

    public void delete() {
        File warpFile = new File(Advanced_warp.getInstance().getDataFolder() + "/warps", this.name + ".yml");
        for (int i = 0; i < Warputils.warps.size(); i++) {
            Warp w = Warputils.warps.get(i);
            if (w.getName().equalsIgnoreCase(this.name))
                Warputils.warps.remove(w);
        }
        warpFile.delete();
    }
}