package de.jeff_media.doorsreloaded.listeners;

import de.jeff_media.doorsreloaded.Main;
import de.jeff_media.doorsreloaded.config.Config;
import de.jeff_media.doorsreloaded.config.Permissions;
import de.jeff_media.doorsreloaded.utils.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.TrapDoor;
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DoorListener implements Listener {

    private final HashMap<Block,Long> autoClose = new HashMap<>();

    private final Main main = Main.getInstance();

    {
        Bukkit.getScheduler().runTaskTimer(main, () -> {
            Iterator<Map.Entry<Block,Long>> it = autoClose.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry<Block,Long> entry = it.next();
                Block block = entry.getKey();
                Long time = entry.getValue();
                if(System.currentTimeMillis() < time) continue;
                if(block.getBlockData() instanceof Openable) {
                    Openable openable = (Openable) block.getBlockData();
                    if(openable.isOpen()) {
                        if(openable instanceof Door) {
                            Block otherDoor = main.getOtherPart((Door) openable, block);
                            if(otherDoor != null) {
                                main.toggleOtherDoor(block, otherDoor, false, false);
                            } else {
                                //System.out.println("other door is null");
                            }
                        }
                        openable.setOpen(false);
                        block.setBlockData(openable);
                        block.getWorld().playEffect(block.getLocation(),Effect.IRON_DOOR_CLOSE, 0);
                    }
                }
                it.remove();
            }
        },1,1);
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIronDoor(PlayerInteractEvent event) {
        if(event.getHand() != EquipmentSlot.HAND) return;
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if(block.getType() != Material.IRON_DOOR && block.getType() != Material.IRON_TRAPDOOR) return;
        if(!main.getConfig().getBoolean(Config.ALLOW_IRONDOORS)) return;
        if(!event.getPlayer().hasPermission(Permissions.IRONDOORS)) return;
        block.getWorld().playEffect(block.getLocation(), Effect.IRON_DOOR_TOGGLE, 0);
        Openable door = (Openable) block.getBlockData();
        door.setOpen(!door.isOpen());
        onRightClickDoor(event);
        block.setBlockData(door);
        autoClose.put(block,System.currentTimeMillis() + (main.getConfig().getLong("autoclose")*1000));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRightClickDoor(PlayerInteractEvent event) {

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.useInteractedBlock() == Event.Result.DENY || event.useItemInHand() == Event.Result.DENY) {
            //System.out.println("not allowed");
            return;
        }
        if(!event.getPlayer().hasPermission(Permissions.USE)) {
            return;
        }
        if (!main.getConfig().getBoolean(Config.ALLOW_DOUBLEDOORS)) {
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        //Material type = clickedBlock.getType();
        BlockData blockData = clickedBlock.getBlockData();
        if (!(blockData instanceof Door)) {
            //System.out.println("not a door");
            return;
        }
        Door door = main.getBottomDoor((Door) blockData, clickedBlock);

        Block otherDoorBlock = main.getOtherPart(door, clickedBlock);

        if (otherDoorBlock == null) {
            //System.out.println("other door is null");
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

        if(block.getBlockData() instanceof Door) {
            SoundUtils.playKnockSound(block);
            //main.debug("This is no door");
        }
        else if (block.getBlockData() instanceof TrapDoor && main.getConfig().getBoolean(Config.ALLOW_KNOCKING_TRAPDOORS)) {
            SoundUtils.playKnockSound(block);
            //main.debug("This is not trapdoor");
        }

        return;
    }
}
