package de.jeff_media.doorsreloaded.listeners;

import de.jeff_media.doorsreloaded.Main;
import de.jeff_media.doorsreloaded.config.Config;
import de.jeff_media.doorsreloaded.config.Permissions;
import de.jeff_media.doorsreloaded.utils.SoundUtils;
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

        main.toggleOtherDoor(block, otherDoorBlock, event.getNewCurrent() > 0, true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRightClickDoor(PlayerInteractEvent event) {

        if(!event.getPlayer().hasPermission(Permissions.USE)) return;
        if (event.useInteractedBlock() == Event.Result.DENY || event.useItemInHand() == Event.Result.DENY) return;
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

        main.toggleOtherDoor(clickedBlock, otherDoorBlock, !otherDoor.isOpen(), false);
    }

    @EventHandler
    public void onDoorKnock(PlayerInteractEvent event) {
        if(!event.getPlayer().hasPermission(Permissions.KNOCK)) return;
        if(!main.getConfig().getBoolean(Config.ALLOW_KNOCKING)) return;
        if(event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if(event.getHand() != EquipmentSlot.HAND) return;
        if(main.getConfig().getBoolean(Config.KNOCKING_REQUIRES_SHIFT) && !event.getPlayer().isSneaking()) return;
        Block block = event.getClickedBlock();
        if(block == null) return;
        if(!(block.getBlockData() instanceof Door)) return;
        SoundUtils.playKnockSound(block);
    }
}
