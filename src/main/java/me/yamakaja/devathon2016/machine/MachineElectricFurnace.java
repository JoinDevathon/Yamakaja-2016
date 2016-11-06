package me.yamakaja.devathon2016.machine;

import me.yamakaja.devathon2016.DevathonPlugin;
import me.yamakaja.devathon2016.electricity.ElectricityNetwork;
import me.yamakaja.devathon2016.electricity.IElectricityReceiver;
import me.yamakaja.devathon2016.inventory.IInventory;
import me.yamakaja.devathon2016.inventory.ISidedInventory;
import me.yamakaja.devathon2016.util.BurnValueRegistry;
import me.yamakaja.devathon2016.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;

/**
 * Created by Yamakaja on 05.11.16.
 */
public class MachineElectricFurnace extends Machine implements IElectricityReceiver, IInventory, ISidedInventory {

    private static final int MAX_ENERGY = 100000;

    private Inventory inventory = Bukkit.createInventory(null, 18, getType().getName());
    private int electricityBuffer;
    private ElectricityNetwork electricityNetwork;


    @Override
    public MachineType getType() {
        return MachineType.ELECTRIC_FURNACE;
    }

    @Override
    public void update() {
        ItemStack result;
        if (electricityBuffer >= 20) {
            for (int i = 0; i < 9; i++) {
                if (inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR && (result = getSmeltingResult(inventory.getItem(i))) != null) {
                    ItemStack toSmelt = inventory.getItem(i);
                    int freeSlot = getFreeTargetSlot(result);
                    if (freeSlot < 0) {
                        continue;
                    }
                    if (toSmelt.getAmount() == 1) {
                        inventory.setItem(i, null);
                    } else {
                        toSmelt.setAmount(toSmelt.getAmount() - 1);
                    }
                    electricityBuffer -= 20;
                    getLocation().getWorld().spawnParticle(Particle.FLAME, getLocation().clone().add(0.5, 0.5, 0.5), 10, 0, 0, 0, 0.05);
                    ItemStack targetSlot = inventory.getItem(freeSlot);
                    if (targetSlot == null) {
                        inventory.setItem(freeSlot, result);
                        return;
                    }
                    targetSlot.setAmount(targetSlot.getAmount() + 1);
                    inventory.setItem(freeSlot, targetSlot);
                    return;
                }
            }
        }
    }

    private ItemStack getSmeltingResult(ItemStack from) {
        Iterator<Recipe> recipeInterator = DevathonPlugin.getInstance().getServer().recipeIterator();

        while (recipeInterator.hasNext()) {
            Recipe recipe = recipeInterator.next();
            if (!(recipe instanceof FurnaceRecipe))
                continue;

            if (fuzzyIsSimilar(((FurnaceRecipe) recipe).getInput(), from)) {
                return recipe.getResult();
            }

        }

        return null;
    }

    private boolean fuzzyIsSimilar(ItemStack a, ItemStack b) {
        if (a.getDurability() == Short.MAX_VALUE)
            a.setDurability((short) 0);

        if (b.getDurability() == Short.MAX_VALUE)
            b.setDurability((short) 0);

        return a.isSimilar(b);
    }

    private int getFreeTargetSlot(ItemStack item) {
        for (int i = 9; i < 18; i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR ||
                    (item.isSimilar(inventory.getItem(i)) &&
                            inventory.getItem(i).getAmount() < inventory.getItem(i).getType().getMaxStackSize())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onBreak(Player player) {
        InventoryUtils.dropInventory(inventory, getLocation());
        if (electricityNetwork != null) {
            electricityNetwork.unregister(this);
            return;
        }
        System.out.println("Furnace had null-network!");
    }

    @Override
    public void onPlace(Player player) {
        super.onPlace(player);
        electricityNetwork = DevathonPlugin.getInstance().getElectricityManager().getNetwork(player.getUniqueId().toString());
        electricityNetwork.register(this);
    }

    @Override
    public void setOwner(String owner) {
        super.setOwner(owner);
        electricityNetwork = DevathonPlugin.getInstance().getElectricityManager().getNetwork(owner);
        electricityNetwork.register(this);
    }

    public ElectricityNetwork getElectricityNetwork() {
        return electricityNetwork;
    }

    @Override
    public void onInteract(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public ItemStack[] getInventory() {
        return inventory.getContents();
    }

    @Override
    public void setContents(ItemStack[] items) {
        inventory.setContents(items);
    }

    @Override
    public int insertItems(ItemStack stack, BlockFace intoSide) {
        if (stack == null || intoSide != BlockFace.UP || getSmeltingResult(stack) == null)
            return 0;

        for(int i = 0; i < 9; i++) {
            ItemStack inventoryItem = inventory.getItem(i);
            if(inventoryItem == null){
                inventory.setItem(i, stack);
                System.out.println(stack.getAmount());
                return stack.getAmount();
            }

            if(inventoryItem.isSimilar(stack)) {
                int maxAmount = stack.getType().getMaxStackSize() - inventoryItem.getAmount();

                int amount = Math.min(stack.getAmount(), maxAmount);

                inventoryItem.setAmount(inventoryItem.getAmount() + amount);
                inventory.setItem(i, inventoryItem);
                return amount;
            }
        }

        return 0;
    }

    @Override
    public ItemStack extractItems(BlockFace fromSide, ItemStack onto) {
        if (fromSide != BlockFace.UP) {
            for (int i = 9; i < 18; i++) {
                ItemStack stack = inventory.getItem(i);

                if(stack == null)
                    continue;

                if(onto == null) {
                    inventory.setItem(i, null);
                    return stack;
                }

                if (onto.isSimilar(stack)) {
                    int maxAmount = onto.getType().getMaxStackSize() - onto.getAmount();
                    if (stack.getAmount() <= maxAmount) {
                        onto.setAmount(onto.getAmount() + stack.getAmount());
                        inventory.setItem(i, null);
                    } else {
                        onto.setAmount(onto.getAmount() + maxAmount);
                        stack.setAmount(stack.getAmount() - maxAmount);
                        inventory.setItem(i, stack);
                    }
                    return onto;
                }
            }
        }
        return null;
    }

    @Override
    public int getDemand() {
        return MAX_ENERGY - electricityBuffer;
    }

    @Override
    public int acceptEnergy(int amount) {
        int actualAmount = Math.min(MAX_ENERGY - electricityBuffer, amount);
        electricityBuffer += actualAmount;
        return actualAmount;
    }

    @Override
    public ElectricityNetwork getNetwork() {
        return null;
    }

    @Override
    public int getBuffer() {
        return electricityBuffer;
    }

    @Override
    public void setBuffer(int buffer) {
        this.electricityBuffer = buffer;
    }
}
