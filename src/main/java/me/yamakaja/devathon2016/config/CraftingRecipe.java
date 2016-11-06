package me.yamakaja.devathon2016.config;

import me.yamakaja.devathon2016.machine.MachineType;
import org.bukkit.Material;

/**
 * Created by Yamakaja on 05.11.16.
 */
public class CraftingRecipe {

    public MachineType machineType;

    public String[] shape;
    public Ingredient[] ingredients;

    public static class Ingredient {
        public String symbol;
        public Material materialType;
    }

}
