package me.yamakaja.devathon2016.util;

import org.bukkit.Material;

/**
 * Created by Yamakaja on 05.11.16.
 */
public class BurnValueRegistry {

    public static int getBurnTime(Material material) {
        switch (material) {
            case WOOD:
            case WOOD_AXE:
            case WOOD_BUTTON:
            case WOOD_DOOR:
            case WOOD_DOUBLE_STEP:
            case WOOD_HOE:
            case WOOD_PICKAXE:
            case WOOD_PLATE:
            case WOOD_SPADE:
            case WOOD_STAIRS:
            case WOOD_STEP:
            case WOOD_SWORD:
            case STICK:
                return 10;
            case COAL:
                return 100;
            case COAL_BLOCK:
                return 900;
            default:
                return 0;
        }
    }

    public static int getBurnEnergy(Material material) {
        switch (material) {
            case WOOD:
            case WOOD_AXE:
            case WOOD_BUTTON:
            case WOOD_DOOR:
            case WOOD_DOUBLE_STEP:
            case WOOD_HOE:
            case WOOD_PICKAXE:
            case WOOD_PLATE:
            case WOOD_SPADE:
            case WOOD_STAIRS:
            case WOOD_STEP:
            case WOOD_SWORD:
            case STICK:
                return 10;
            case COAL:
                return 200;
            case COAL_BLOCK:
                return 1800;
            default:
                return 0;
        }
    }

}
