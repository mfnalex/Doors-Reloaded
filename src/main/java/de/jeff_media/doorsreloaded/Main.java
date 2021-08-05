package de.jeff_media.doorsreloaded;

import de.jeff_media.doorsreloaded.commands.ReloadCommand;
import de.jeff_media.doorsreloaded.config.Config;
import de.jeff_media.doorsreloaded.data.PossibleNeighbour;
import de.jeff_media.doorsreloaded.listeners.DoorListener;
import de.jeff_media.updatechecker.UpdateChecker;
import de.jeff_media.updatechecker.UserAgentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin {

    private static Main instance;
    private static final PossibleNeighbour[] possibleNeighbours = new PossibleNeighbour[] {
            new PossibleNeighbour(0, -1, Door.Hinge.RIGHT, BlockFace.EAST),
            new PossibleNeighbour(0, 1, Door.Hinge.LEFT, BlockFace.EAST),

            new PossibleNeighbour(1,0, Door.Hinge.RIGHT, BlockFace.SOUTH),
            new PossibleNeighbour(-1,0, Door.Hinge.LEFT, BlockFace.SOUTH),

            new PossibleNeighbour(0, 1, Door.Hinge.RIGHT, BlockFace.WEST),
            new PossibleNeighbour(0, -1, Door.Hinge.LEFT, BlockFace.WEST),

            new PossibleNeighbour(-1,0, Door.Hinge.RIGHT, BlockFace.NORTH),
            new PossibleNeighbour(1,0, Door.Hinge.LEFT, BlockFace.NORTH)
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


    public Block getOtherPart(Door door, Block block) {
        if(door == null) return null;
        for (PossibleNeighbour neighbour : possibleNeighbours) {
            if(neighbour.getFacing() != door.getFacing()) continue;
            if (neighbour.getHinge() != door.getHinge()) continue;
            Block relative = block.getRelative(neighbour.getOffsetX(), 0, neighbour.getOffsetZ());
            if (relative.getType() != block.getType()) continue;
            if (!(relative.getBlockData() instanceof Door)) continue;
            Door otherDoor = ((Door) relative.getBlockData());
            if (otherDoor.getHinge() == neighbour.getHinge()) continue;
            if(door.isOpen() != otherDoor.isOpen()) continue;
            if(otherDoor.getFacing() != neighbour.getFacing()) continue;
            return relative;
        }
        return null;
    }

    private void initUpdateChecker() {
        UpdateChecker.init(this, "https://api.jeff-media.de/doorsreloaded/latest-version.txt")
                .setNotifyRequesters(true)
                .setDownloadLink(91722)
                .setChangelogLink(91722)
                .setUserAgent(UserAgentBuilder.getDefaultUserAgent())
                .setDonationLink("https://paypal.me/mfnalex")
                .suppressUpToDateMessage(true);

        if (getConfig().getString(Config.CHECK_FOR_UPDATES).equalsIgnoreCase("true")) {
            UpdateChecker.getInstance().checkEveryXHours(getConfig().getDouble(Config.CHECK_FOR_UPDATES_INTERVAL)).checkNow();
        } else if (getConfig().getString(Config.CHECK_FOR_UPDATES).equalsIgnoreCase("on-startup")) {
            UpdateChecker.getInstance().checkNow();
        } else {
            UpdateChecker.getInstance().setNotifyOpsOnJoin(false);
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
        getCommand("doorsreloaded").setExecutor(new ReloadCommand());
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

    public void toggleOtherDoor(Block block, Block otherBlock, boolean open, boolean causedByRedstone) {

        if (!(block.getBlockData() instanceof Door)) return;
        if (!(otherBlock.getBlockData() instanceof Door)) return;

        Door door = (Door) block.getBlockData();
        Door otherDoor = (Door) otherBlock.getBlockData();

        if (causedByRedstone) {
            toggleDoor(otherBlock, otherDoor, open);
            return;
        }

        boolean openNow = door.isOpen();
        new BukkitRunnable() {
            @Override
                public void run() {
                if (!(otherBlock.getBlockData() instanceof Door)) return;
                Door newDoor = (Door) block.getBlockData();
                if (newDoor.isOpen() == openNow) {
                    return;
                }
                toggleDoor(otherBlock, otherDoor, open);
            }
            }.runTaskLater(this, 1L);

    }

}
