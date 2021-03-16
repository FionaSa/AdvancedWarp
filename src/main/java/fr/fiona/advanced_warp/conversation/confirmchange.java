package fr.fiona.advanced_warp.conversation;

import fr.fiona.advanced_warp.Advanced_warp;
import fr.fiona.advanced_warp.utils.Warp;
import fr.fiona.advanced_warp.utils.Warputils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class confirmchange extends StringPrompt {

    Player player;
    String name;

    public confirmchange(Player player,String name) {
        //  player.sendRawMessage("test");
        this.player = player;
        this.name = name;
        //  player.sendRawMessage("test2");
    }

    @Override
    public String getPromptText(ConversationContext context) {
        // player.sendRawMessage("test3");
        return Advanced_warp.getInstance().language.getLanguageConfig().getString("confirm-change");
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if(input.equalsIgnoreCase(Advanced_warp.getInstance().language.getLanguageConfig().getString("yes-message")))
        {

            for(Warp w: Warputils.getWarps(player.getUniqueId()))
            {
                if (w.getName().equalsIgnoreCase(name))
                {
                    w.setLocation(player.getLocation());
                }
            }


            Advanced_warp.getEconomy().withdrawPlayer(player, Advanced_warp.getInstance().getConfig().getInt("warp-cost-change"));

            player.sendRawMessage(ChatColor.AQUA+Advanced_warp.getInstance().language.getLanguageConfig().getString("prefix")+Advanced_warp.getInstance().language.getLanguageConfig().getString("warp-changed"));

        }
        return END_OF_CONVERSATION;
    }
}
