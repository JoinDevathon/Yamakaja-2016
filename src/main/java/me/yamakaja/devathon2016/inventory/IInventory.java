package me.yamakaja.devathon2016.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Yamakaja on 05.11.16.
 */
public interface IInventory {

    public ItemStack[] getInventory();

    public void setContents(ItemStack[] items);

}
