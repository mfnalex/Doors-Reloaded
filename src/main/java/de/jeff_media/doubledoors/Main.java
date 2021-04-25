package de.jeff_media.doubledoors;

import de.jeff_media.doubledoors.commands.ReloadCommand;
import de.jeff_media.doubledoors.config.Config;
import de.jeff_media.doubledoors.data.PossibleNeighbour;
import de.jeff_media.doubledoors.listeners.DoorListener;
import de.jeff_media.updatechecker.UpdateChecker;
import de.jeff_media.updatechecker.UserAgentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

public class Main extends JavaPlugin {

    private static Main instance;
    private static final PossibleNeighbour[] possibleNeighbours = new PossibleNeighbour[] {
            // North/South, West/East are interchangeable
            new PossibleNeighbour(-1, 0, Door.Hinge.RIGHT, BlockFace.SOUTH),
            new PossibleNeighbour(-1, 0, Door.Hinge.LEFT, BlockFace.NORTH),


            new PossibleNeighbour(+1, 0, Door.Hinge.LEFT, BlockFace.SOUTH),
            new PossibleNeighbour(+1, 0, Door.Hinge.RIGHT, BlockFace.NORTH),


            new PossibleNeighbour(0, +1, Door.Hinge.RIGHT, BlockFace.WEST),
            new PossibleNeighbour(0, +1, Door.Hinge.LEFT, BlockFace.EAST),


            new PossibleNeighbour(0, -1, Door.Hinge.LEFT, BlockFace.WEST),
            new PossibleNeighbour(0, -1, Door.Hinge.RIGHT, BlockFace.EAST)
    };
    private boolean redstoneEnabled = false;

    public static Main getInstance() {
        return instance;
    }

    public void debug(String text) {
        if (getConfig().getBoolean(Config.DEBUG)) {
            getLogger().warning("[DEBUG] " + text);
        }
    }

    @Nullable
    public Door getBottomDoor(Door door, Block block) {

        if (door.getHalf() == Bisected.Half.BOTTOM) {
            return door;
        }

        Block below = block.getRelative(BlockFace.DOWN);
        if (below.getType() != block.getType()) return null; // Door is obviously broken

        if (below.getBlockData() instanceof Door) {
            return (Door) below.getBlockData();
        }

        return null; // Door is not matching
    }

    @Nullable
    public Block getOtherPart(Door door, Block block) {
        for (PossibleNeighbour neighbour : possibleNeighbours) {
            if (neighbour.getHinge() == door.getHinge()) continue;
            Block relative = block.getRelative(neighbour.getOffsetX(), 0, neighbour.getOffsetZ());
            if (relative.getType() != block.getType()) continue;
            if (!(relative.getBlockData() instanceof Door)) continue;
            Door otherDoor = ((Door) relative.getBlockData());
            if (otherDoor.getHinge() == neighbour.getHinge() && door.isOpen() == otherDoor.isOpen()) {
                return relative;
            }
        }
        return null;
    }

    public @Nullable Block getOtherPart2(Door door, Block block) {

        for (PossibleNeighbour neighbour : possibleNeighbours) {

            if (neighbour.getHinge() == door.getHinge()) continue;
            Block relative = block.getRelative(neighbour.getOffsetX(), 0, neighbour.getOffsetZ());
            if (relative.getType() != block.getType()) continue;
            if (!(relative.getBlockData() instanceof Door)) continue;
            Door otherDoor = (Door) relative.getBlockData();
            if (otherDoor.getHinge() != neighbour.getHinge()) continue; // Hinges don't match
            if (door.isOpen() != ((Door) relative.getBlockData()).isOpen())
                continue; // One door is open, the other closed
            return relative;
        }
        return null;
    }

    private void initUpdateChecker() {
        UpdateChecker.init(this, "https://api.jeff-media.de/doubledoors/latest-version.txt")
                .setNotifyRequesters(true)
                .setUserAgent(UserAgentBuilder.getDefaultUserAgent())
                .setDonationLink("https://paypal.me/mfnalex");

        if (getConfig().getString(Config.CHECK_FOR_UPDATES).equalsIgnoreCase("true")) {
            UpdateChecker.getInstance().checkEveryXHours(getConfig().getDouble(Config.CHECK_FOR_UPDATES_INTERVAL)).checkNow();
        } else if (getConfig().getString(Config.CHECK_FOR_UPDATES).equalsIgnoreCase("on-startup")) {
            UpdateChecker.getInstance().checkNow();
        }
    }

    public boolean isDebug() {
        return getConfig().getBoolean(Config.DEBUG);
    }

    public boolean isRedstoneEnabled() {
        return redstoneEnabled;
    }

    @Override
    public void onEnable() {
        instance = this;
        Config.init();
        reload();
        Bukkit.getPluginManager().registerEvents(new DoorListener(), this);
        initUpdateChecker();
        getCommand("doubledoors").setExecutor(new ReloadCommand());
    }

    public void reload() {
        saveDefaultConfig();
        reloadConfig();
        redstoneEnabled = getConfig().getBoolean(Config.CHECK_FOR_REDSTONE);
    }

    private void toggleDoor(Block otherDoorBlock, Door otherDoor, boolean open) {
        otherDoor.setOpen(open);
        otherDoorBlock.setBlockData(otherDoor);
    }

    public void toggleOtherDoor(Block doorBlock, Door door, Block otherDoorBlock, Door otherDoor, boolean open, boolean causedByRedstone) {

        if (causedByRedstone) {
            toggleDoor(otherDoorBlock, otherDoor, open);
            return;
        }

        if (getConfig().getBoolean(Config.CHECK_OTHER_PLUGINS)) {

            boolean openNow = door.isOpen();
            debug("Open now: " + openNow);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(!(otherDoorBlock.getBlockData() instanceof Door)) return;
                    Door newDoor = (Door) doorBlock.getBlockData();
                    debug("Open 1 tick later: " + newDoor.isOpen());
                    if (newDoor.isOpen() == openNow) {
                        debug("Check other plugins -> cancelled");
                        return;
                    }
                    debug("Check other plugins -> success");
                    toggleDoor(otherDoorBlock, otherDoor, open);
                }
            }.runTaskLater(this, 1L);
        } else {
            if (door.getMaterial() == Material.IRON_DOOR) {
                return;
            }
            toggleDoor(otherDoorBlock, otherDoor, open);
        }
    }

}
