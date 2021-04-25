package de.jeff_media.doubledoors.listeners;

import de.jeff_media.doubledoors.Main;
import de.jeff_media.doubledoors.config.Config;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class DoorListener implements Listener {

    private final Main main;

    public DoorListener() {
        main = Main.getInstance();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRedstoneDoor(BlockRedstoneEvent event) {
        if (!main.isRedstoneEnabled()) {
            return;
        }
        Block block = event.getBlock();
        if (!(block.getBlockData() instanceof Door)) return;
        Door door = (Door) block.getBlockData();
        if (event.getNewCurrent() > 0 && event.getOldCurrent() > 0) {
            return;
        }

        Block otherDoorBlock = main.getOtherPart(door, block);
        if (otherDoorBlock == null) {
            return;
        }

        if (otherDoorBlock.getBlockPower() > 0) return;

        main.toggleOtherDoor(door, otherDoorBlock, (Door) otherDoorBlock.getBlockData(), event.getNewCurrent() > 0, true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRightClickDoor(PlayerInteractEvent event) {

        if (main.getConfig().getBoolean(Config.CHECK_FOR_PROTECTION_PLUGINS)) {
            if (event.useInteractedBlock() == Event.Result.DENY || event.useItemInHand() == Event.Result.DENY) return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        //Material type = clickedBlock.getType();
        BlockData blockData = clickedBlock.getBlockData();
        if (!(blockData instanceof Door)) return;
        Door door = main.getBottomDoor((Door) blockData, clickedBlock);

        Block otherDoorBlock = main.getOtherPart(door, clickedBlock);

        if (otherDoorBlock == null) {
            return;
        }

        Door otherDoor = (Door) otherDoorBlock.getBlockData();

        main.toggleOtherDoor(door, otherDoorBlock, otherDoor, !otherDoor.isOpen(), false);
    }
}
