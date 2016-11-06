package me.yamakaja.devathon2016.machine;

import me.yamakaja.devathon2016.DevathonPlugin;
import me.yamakaja.devathon2016.electricity.ElectricityNetwork;
import me.yamakaja.devathon2016.electricity.IElectricityProvider;
import me.yamakaja.devathon2016.inventory.IInventory;
import me.yamakaja.devathon2016.inventory.ISidedInventory;
import me.yamakaja.devathon2016.util.BurnValueRegistry;
import me.yamakaja.devathon2016.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Yamakaja on 05.11.16.
 */
public class MachineGenerator extends Machine implements IElectricityProvider, ISidedInventory, IInventory {

    private static final int MAX_ENERGY = 100000;

    private ElectricityNetwork electricityNetwork;
    private int energyBuffer;
    private int progress = -1;
    private int resultingEnergy;
    private Inventory inventory = Bukkit.createInventory(null, 9, getType().getName());

    public MachineGenerator() {
    }

    @Override
    public MachineType getType() {
        return MachineType.GENERATOR;
    }

    @Override
    public void onBreak(Player player) {
        InventoryUtils.dropInventory(inventory, getLocation());
        if(electricityNetwork != null) {
            electricityNetwork.unregister(this);
            return;
        }
        System.out.println("Generator had no electricity electricityNetwork");
    }

    @Override
    public void onPlace(Player player) {
        super.onPlace(player);
        this.electricityNetwork = DevathonPlugin.getInstance().getElectricityManager().getNetwork(player.getUniqueId().toString());
        electricityNetwork.register(this);
    }

    @Override
    public void setOwner(String owner) {
        super.setOwner(owner);
        electricityNetwork = DevathonPlugin.getInstance().getElectricityManager().getNetwork(owner);
        electricityNetwork.register(this);
    }

    @Override
    public void onInteract(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public void update() {

        distributeEnergy();

        if (energyBuffer >= MAX_ENERGY)
            return;

        doProgress();

    }

    private void distributeEnergy() {
        for (BlockFace face : BlockFace.values()) {
            Block relative = getLocation().getBlock().getRelative(face);

        }
    }

    private void doProgress() {
        if (progress == 0) {

            energyBuffer += resultingEnergy;
            resultingEnergy = 0;
            progress = -1;
            return;
        }

        if (progress == -1) {

            int slot = -1;
            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItem(i) != null && BurnValueRegistry.getBurnTime(inventory.getItem(i).getType()) > 0) {
                    slot = i;
                    break;
                }
            }

            if (slot < 0)
                return;

            progress = BurnValueRegistry.getBurnTime(inventory.getItem(slot).getType());
            resultingEnergy = BurnValueRegistry.getBurnEnergy(inventory.getItem(slot).getType());
            if (inventory.getItem(slot).getAmount() == 1)
                inventory.setItem(slot, null);
            else
                inventory.getItem(slot).setAmount(inventory.getItem(slot).getAmount() - 1);

        }

        if (progress > 0) {
            --progress;
            getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, getLocation().clone().add(0.5, 1, 0.5), 2, 0.5, 0.5, 0.5, 0.1);
        }
    }

    @Override
    public int pullEnergy(int maxAmount) {
        maxAmount = Math.min(maxAmount, energyBuffer);
        energyBuffer -= maxAmount;
        return maxAmount;
    }

    @Override
    public int getBuffer() {
        return energyBuffer;
    }

    @Override
    public void setBuffer(int amount) {
        this.energyBuffer = amount;
    }

    @Override
    public int getMaxBufferSize() {
        return MAX_ENERGY;
    }

    @Override
    public int insertItems(ItemStack stack, BlockFace intoSide) {
        return 0;
    }

    @Override
    public ItemStack extractItems(BlockFace fromSide, ItemStack filter) {
        return null;
    }

    @Override
    public void setContents(ItemStack[] items) {
        this.inventory.setContents(items);
    }

    @Override
    public ItemStack[] getInventory() {
        return inventory.getContents();
    }

    @Override
    public ElectricityNetwork getNetwork() {
        return electricityNetwork;
    }
}
