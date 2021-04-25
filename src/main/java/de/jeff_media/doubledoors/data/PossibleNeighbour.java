package de.jeff_media.doubledoors.data;

import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Door;

public class PossibleNeighbour {
    private final BlockFace facing;
    private final Door.Hinge hinge;
    private final int offsetX;
    private final int offsetZ;

    public PossibleNeighbour(int offsetX, int offsetZ, Door.Hinge hinge, BlockFace facing) {
        this.offsetX = offsetX;
        this.offsetZ = offsetZ;
        this.hinge = hinge;
        this.facing = facing;
    }

// --Commented out by Inspection START (25.04.2021 20:54):
//    public BlockFace getFacing() {
//        return facing;
//    }
// --Commented out by Inspection STOP (25.04.2021 20:54)

    public Door.Hinge getHinge() {
        return hinge;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetZ() {
        return offsetZ;
    }

    @Override
    public String toString() {
        return "PossibleNeighbour{" +
                "offsetX=" + offsetX +
                ", offsetZ=" + offsetZ +
                ", facing=" + facing +
                ", hinge=" + hinge +
                '}';
    }
}
