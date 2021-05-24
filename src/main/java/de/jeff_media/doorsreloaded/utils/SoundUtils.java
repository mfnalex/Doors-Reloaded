package de.jeff_media.doorsreloaded.utils;

import com.google.common.base.Enums;
import de.jeff_media.doorsreloaded.Main;
import de.jeff_media.doorsreloaded.config.Config;
import org.bukkit.*;
import org.bukkit.block.Block;

public class SoundUtils {

    public static void playKnockSound(Block block) {
        Main.getInstance().debug("Finally playing sound");
        Main main = Main.getInstance();
        Location location = block.getLocation();
        World world = block.getWorld();
        Sound sound = block.getType() == Material.IRON_DOOR
                ? Enums.getIfPresent(Sound.class, main.getConfig().getString(Config.SOUND_KNOCK_IRON)).or(Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR)
                : Enums.getIfPresent(Sound.class, main.getConfig().getString(Config.SOUND_KNOCK_WOOD)).or(Sound.ITEM_SHIELD_BLOCK);
        SoundCategory category = Enums.getIfPresent(SoundCategory.class,main.getConfig().getString(Config.SOUND_KNOCK_CATEGORY)).or(SoundCategory.BLOCKS);
        float volume = (float) main.getConfig().getDouble(Config.SOUND_KNOCK_VOLUME);
        float pitch = (float) main.getConfig().getDouble(Config.SOUND_KNOCK_PITCH);
        world.playSound(location,sound, category,volume,pitch);
        Main.getInstance().debug("World: " + world);
        Main.getInstance().debug("Location: " + location);
        Main.getInstance().debug("Sound: " + sound.name());
        Main.getInstance().debug("Category: " + category.name());
        Main.getInstance().debug("Volume: " + volume);
        Main.getInstance().debug("Pitch: " + pitch);
    }

}
