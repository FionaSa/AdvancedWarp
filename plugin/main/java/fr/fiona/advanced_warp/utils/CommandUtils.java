package fr.fiona.advanced_warp.utils;

import com.google.common.collect.ImmutableList;
import fr.fiona.advanced_warp.Advanced_warp;

public class CommandUtils {
    private static void registerCommandCompletions() {
       // Advanced_warp.manager.getCommandReplacements().addReplacement("empirecommand", "empire|empires|emp|guild|clan|e|towny");

        Advanced_warp.manager.getCommandCompletions().registerCompletion("nomwarp", c -> {
            return ImmutableList.of(Warputils.warps.toString());
        });
    }
}
