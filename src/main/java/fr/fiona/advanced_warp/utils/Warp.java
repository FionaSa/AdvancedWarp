package fr.fiona.advanced_warp.utils;

import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface Warp {
    Location getLocation();

    List<String> getVisitors();

    List<String> getBlackList();

     void addBlackList(Player p,String name);

    Long getLastvisit();

    Long getTimecreated();


    String getCategory();
    
    String getTexture();

    void setTexture(String paramString);

    void updateLastvisit();
    
    void updateCountVisit();

    void addVisitor(Player p);

    void setLocation(Location location);

    String getName();

    OfflinePlayer getOwner();

    void delete();

    int getCountVisit();

    public int compareTo(Warp w);


    static String getWarpCategory(Warp warp) {
        return warp.getCategory();
    }

    public void setCategory(String value);
}