package me.yamakaja.devathon2016.util;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Created by Yamakaja on 06.11.16.
 */
public class PlayerUtil {

    public static BlockFace getPlayerDirection(Player player) {

        double dotProduct = -1, dotProductTemp;
        BlockFace direction = null;

        for(DirectionVector vector : DirectionVector.values()){
            if((dotProductTemp = player.getLocation().getDirection().dot(vector.getDirectionalVector())) > dotProduct) {
                direction = vector.getDirection();
                dotProduct = dotProductTemp;
            }
        }


        return direction;
    }

    private enum DirectionVector {
        UP(new Vector(0, 1, 0), BlockFace.UP),
        DOWN(new Vector(0, -1, 0), BlockFace.DOWN),
        NORTH(new Vector(0, 0, -1), BlockFace.NORTH),
        EAST(new Vector(1, 0, 0), BlockFace.EAST),
        SOUTH(new Vector(0, 0, 1), BlockFace.SOUTH),
        WEST(new Vector(-1, 0, 0), BlockFace.WEST);

        private Vector directionalVector;
        private BlockFace direction;

        DirectionVector(Vector directionalVector, BlockFace direction) {

            this.directionalVector = directionalVector;
            this.direction = direction;
        }

        public BlockFace getDirection() {
            return direction;
        }

        public Vector getDirectionalVector() {
            return directionalVector;
        }
    }

}
