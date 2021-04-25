package de.jeff_media.doubledoors.config;

import de.jeff_media.doubledoors.Main;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    public static final String CHECK_FOR_REDSTONE = "check-for-redstone";
    public static final String CHECK_FOR_UPDATES = "check-for-updates";
    public static final String CHECK_FOR_UPDATES_INTERVAL = "check-interval";
    public static final String DEBUG = "debug";

    public static void init() {
        //Main main = Main.getInstance();
        FileConfiguration conf = Main.getInstance().getConfig();
        conf.addDefault(CHECK_FOR_UPDATES, "true");
        conf.addDefault(CHECK_FOR_UPDATES_INTERVAL, 4);
        conf.addDefault(CHECK_FOR_REDSTONE, true);
        conf.addDefault(DEBUG, false);
    }
}
