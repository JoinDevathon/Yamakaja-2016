package me.yamakaja.devathon2016.machine;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yamakaja on 05.11.16.
 */
public enum MachineType {

    GENERATOR("Generator", Arrays.asList(ChatColor.AQUA + "This machine produces electricity for burnable",
            ChatColor.AQUA + "items like wooden tools or coal."), Material.FURNACE, MachineGenerator.class),
    ELECTRIC_FURNACE("Electric Furnace", Arrays.asList(ChatColor.AQUA + "This machine smelts items like an ordinary",
            ChatColor.AQUA + "furnace, but a lot faster.", ChatColor.AQUA + "(At the cost of electricity)"),
            Material.IRON_BLOCK, MachineElectricFurnace.class),
    PIPE("Pipe", Arrays.asList(ChatColor.AQUA + "Transports items!", ChatColor.AQUA + "Give it a diamond to enable auto-extract"),
            Material.GLASS, MachinePipe.class),
    QUARRY("Quarry", Arrays.asList(ChatColor.AQUA + "Mines the chunk which it's placed", ChatColor.AQUA + "for resources and spits them out"),
            Material.DIAMOND_BLOCK, MachineQuarry.class);

    private String name;
    private List<String> lore;
    private Material blockType;
    private Class<? extends Machine> machineClass;

    MachineType(String name, List<String> lore, Material blockType, Class<? extends Machine> machineClass) {
        this.name = name;
        this.lore = lore;
        this.blockType = blockType;
        this.machineClass = machineClass;
    }

    public static MachineType getMachineTypeForItemStack(ItemStack block) {
        ItemMeta meta = block.getItemMeta();

        if (!meta.hasDisplayName() || !meta.hasLore())
            return null;

        List<MachineType> machineType = Arrays.stream(values()).filter(type -> type.getName().equals(meta.getDisplayName()) &&
                type.getLore().equals(meta.getLore())).collect(Collectors.toList());

        if(machineType.size() == 1)
            return machineType.get(0);

        return null;

    }

    public List<String> getLore() {
        return lore;
    }

    public String getName() {
        return name;
    }

    public Material getBlockType() {
        return blockType;
    }

    public Class<? extends Machine> getMachineClass() {
        return machineClass;
    }

}
