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
        String soundID;
        if (block.getType().equals(Material.IRON_DOOR)) {
            soundID = main.getConfig().getString(Config.SOUND_KNOCK_IRON);
            if (soundID == null || soundID.isEmpty()) soundID = "minecraft:item.shield.block";
        } else {
            soundID = main.getConfig().getString(Config.SOUND_KNOCK_WOOD);
            if (soundID == null || soundID.isEmpty()) soundID = "minecraft:entity.zombie.attack_iron_door";
        }
        float volume = (float) main.getConfig().getDouble(Config.SOUND_KNOCK_VOLUME);
        float pitch = (float) main.getConfig().getDouble(Config.SOUND_KNOCK_PITCH);
        SoundCategory category = Enums.getIfPresent(SoundCategory.class,main.getConfig().getString(Config.SOUND_KNOCK_CATEGORY)).or(SoundCategory.BLOCKS);
        NamespacedKey soundKey = NamespacedKey.fromString(soundID) != null ? NamespacedKey.fromString(soundID) : NamespacedKey.fromString("minecraft:item.shield.block");
        Sound sound = Registry.SOUNDS.get(soundKey);
        world.playSound(location, sound, category,volume,pitch);
        Main.getInstance().debug("World: " + world);
        Main.getInstance().debug("Location: " + location);
        Main.getInstance().debug("Sound: " + soundID);
        Main.getInstance().debug("Category: " + category.name());
        Main.getInstance().debug("Volume: " + volume);
        Main.getInstance().debug("Pitch: " + pitch);
    }

}
