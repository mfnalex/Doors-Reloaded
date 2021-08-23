package de.jeff_media.doorsreloaded.listeners;

import de.jeff_media.doorsreloaded.Main;
import de.jeff_media.doorsreloaded.config.Config;
import de.jeff_media.doorsreloaded.config.Permissions;
import de.jeff_media.doorsreloaded.utils.SoundUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

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
        //main.debug("Door Knock: " + event.getPlayer().getName());
        Player player = event.getPlayer();
        GameMode gameMode = player.getGameMode();
        if(gameMode == GameMode.CREATIVE || gameMode == GameMode.SPECTATOR) {
            //main.debug("Wrong gamemode");
            return;
        }
        if(!player.hasPermission(Permissions.KNOCK)) {
            //main.debug("No permission");
            return;
        }
        if(!main.getConfig().getBoolean(Config.ALLOW_KNOCKING)) {
            //main.debug("Disabled in config");
            return;
        }
        if(event.getAction() != Action.LEFT_CLICK_BLOCK) {
            //main.debug("No left Click Block");
            return;
        }
        if(event.getHand() != EquipmentSlot.HAND) {
            //main.debug("Wrong slot");
            return;
        }
        if(main.getConfig().getBoolean(Config.KNOCKING_REQUIRES_SHIFT) && !player.isSneaking()) {
            //main.debug("Not sneaking although it's required");
            return;
        }
        if(main.getConfig().getBoolean(Config.KNOCKING_REQUIRES_EMPTY_HAND)) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if(itemInHand.getType() != Material.AIR) {
                //main.debug("Hand not empty although it's required");
                //main.debug("Item in Hand: " + itemInHand);
                return;
            }
        }
        Block block = event.getClickedBlock();
        if(block == null) {
            //main.debug("Block is null");
            return;
        }
        if(!(block.getBlockData() instanceof Door)) {
            //main.debug("This is no door");
            return;
        }
        SoundUtils.playKnockSound(block);
    }
}
