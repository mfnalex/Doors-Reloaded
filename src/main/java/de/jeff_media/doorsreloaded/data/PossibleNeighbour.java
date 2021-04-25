package de.jeff_media.doorsreloaded.data;

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

    public BlockFace getFacing() {
        return facing;
    }

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
