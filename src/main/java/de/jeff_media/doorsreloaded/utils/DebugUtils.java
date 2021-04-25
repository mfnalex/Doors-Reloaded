package de.jeff_media.doorsreloaded.utils;

import org.bukkit.block.Block;

public class DebugUtils {

    public static String loc2str(Block block) {
        String stringBuilder = block.getX() +
                ", " +
                block.getY() +
                ", " +
                block.getZ();
        return stringBuilder;
    }

}
