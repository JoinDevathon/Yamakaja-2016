package me.yamakaja.devathon2016.util;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Created by Yamakaja on 06.11.16.
 */
public class InventoryUtils {

    public static void dropInventory(Inventory inventory, Location location) {
        dropItems(inventory.getContents(), location);
    }

    public static void dropItems(ItemStack[] stacks, Location location) {
        if(stacks == null)
            return;
        Arrays.stream(stacks).filter(item -> item != null).forEach(item ->
                location.getWorld().dropItemNaturally(location, item)
        );
    }

}
