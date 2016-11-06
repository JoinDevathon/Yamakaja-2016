package me.yamakaja.devathon2016.inventory;

import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Yamakaja on 05.11.16.
 */
public interface ISidedInventory {

    /**
     * @param stack    The stack to insert
     * @param intoSide From which side to insert
     * @return The amount of items actually inserted
     */
    int insertItems(ItemStack stack, BlockFace intoSide);

    ItemStack extractItems(BlockFace fromSide, ItemStack filter);

}
