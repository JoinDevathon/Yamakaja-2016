package me.yamakaja.devathon2016.machine;

import me.yamakaja.devathon2016.DevathonPlugin;
import me.yamakaja.devathon2016.inventory.IInventory;
import me.yamakaja.devathon2016.inventory.ISidedInventory;
import me.yamakaja.devathon2016.util.InventoryUtils;
import me.yamakaja.devathon2016.util.ItemBuilder;
import me.yamakaja.devathon2016.util.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Yamakaja on 06.11.16.
 */
public class MachinePipe extends Machine implements IInventory, ISidedInventory {

    private ItemStack[] contents = new ItemStack[2];
    private BlockFace direction;
    private boolean upgraded;

    @Override
    public MachineType getType() {
        return MachineType.PIPE;
    }

    @Override
    public void update() {

        if (upgraded)
            doPull();
        doPush();

    }

    private void doPull() {
        Block toPullFrom = getLocation().getBlock().getRelative(direction.getOppositeFace());
        Machine machine = DevathonPlugin.getInstance().getMachineManager().getMachineAt(toPullFrom.getLocation());

        if (machine != null) {
            if (machine instanceof ISidedInventory) {
                for (int i = 0; i < contents.length; i++) {
                    ItemStack stack = ((ISidedInventory) machine).extractItems(direction, contents[0]);
                    if(stack != null) {
                        contents[0] = stack;

                        showPullParticles();

                        return;
                    }
                }
            }
        } else {
            if (toPullFrom.getState() instanceof InventoryHolder) {
                Inventory inventory = ((InventoryHolder) toPullFrom.getState()).getInventory();

                for (int i = 0; i < inventory.getSize(); i++) {
                    for (int c = 0; c < contents.length; c++) {
                        if (inventory.getItem(i) == null)
                            continue;
                        if (contents[c] == null) {
                            contents[c] = inventory.getItem(i);
                            inventory.setItem(i, null);
                        } else if (inventory.getItem(i).isSimilar(contents[c])) {
                            int maxAmount = contents[c].getType().getMaxStackSize() - contents[c].getAmount();
                            if (maxAmount == 0)
                                continue;

                            int amountToPull = Math.min(maxAmount, inventory.getItem(i).getAmount());


                            if (inventory.getItem(i).getAmount() - amountToPull > 0)
                                inventory.setItem(i, new ItemBuilder(contents[c]).setCount(inventory.getItem(i).getAmount() - amountToPull));
                            else
                                inventory.setItem(i, null);

                            showPullParticles();
                            contents[c] = new ItemBuilder(contents[c]).setCount(contents[c].getAmount() + amountToPull);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void showPullParticles() {
        Location loc = getLocation().clone().add(0.5, 0.5, 0.5);
        for(int x = 0; x < 10; x++) {
            getLocation().getWorld().spawnParticle(Particle.REDSTONE, loc.add(
                    direction.getOppositeFace().getModX()/10D, direction.getOppositeFace().getModY()/10D,
                    direction.getOppositeFace().getModZ()/10D), 1, 0, 0, 0, 0);
        }
    }

    private void showPushParticles() {
        Location loc = getLocation().clone().add(0.5, 0.5, 0.5);
        for(int x = 0; x < 10; x++) {
            getLocation().getWorld().spawnParticle(Particle.REDSTONE, loc.add(
                    direction.getModX()/10D, direction.getModY()/10D,
                    direction.getModZ()/10D), 1, 0, 0, 0, 0);
        }
    }

    private void doPush() {
        Block toPushTo = getLocation().getBlock().getRelative(direction);
        Machine machine = DevathonPlugin.getInstance().getMachineManager().getMachineAt(toPushTo.getLocation());

        if (machine != null) {
            if (machine instanceof ISidedInventory) {
                for (int i = 0; i < contents.length; i++) {
                    if(contents[i] == null)
                        continue;
                    int amount = ((ISidedInventory) machine).insertItems(contents[i], direction.getOppositeFace());
                    if (amount == 0)
                        continue;
                    if (contents[i].getAmount() - amount == 0) {
                        contents[i] = null;
                        showPushParticles();
                        return;
                    }
                    contents[i].setAmount(contents[i].getAmount() - amount);
                }
            }
        } else if (toPushTo.getState() instanceof InventoryHolder) {
            ItemStack leftover;
            for (int i = 0; i < contents.length; i++) {
                try {
                    if(contents[i] == null)
                        continue;
                    leftover = ((InventoryHolder) toPushTo.getState()).getInventory().addItem(contents[i]).values().iterator().next();
                    if (leftover.getAmount() == contents[i].getAmount())
                        continue;

                    contents[i] = leftover;
                    showPushParticles();
                } catch (Exception e) {
                    contents[i] = null;
                    showPushParticles();
                    break;
                }
            }
        }
    }

    @Override
    public void onBreak(Player player) {
        InventoryUtils.dropItems(contents, getLocation());
        if (upgraded)
            getLocation().getWorld().dropItemNaturally(getLocation(), new ItemStack(Material.DIAMOND, 1));
    }

    @Override
    public void onPlace(Player player) {
        super.onPlace(player);
        this.direction = PlayerUtil.getPlayerDirection(player).getOppositeFace();
    }

    @Override
    public void onInteract(Player player) {
        if (upgraded)
            return;
        ItemStack stack = player.getItemInHand();

        if (stack != null && stack.getType() == Material.DIAMOND) {
            upgraded = true;
            if (stack.getAmount() == 1) {
                player.setItemInHand(null);
                return;
            }
            stack.setAmount(stack.getAmount() - 1);
        }
    }

    @Override
    public ItemStack[] getInventory() {
        return contents;
    }

    @Override
    public void setContents(ItemStack[] items) {
        this.contents = items;
    }

    public BlockFace getDirection() {
        return direction;
    }

    @Override
    public String getExtra() {
        return direction.toString() + "|" + Boolean.toString(upgraded);
    }

    @Override
    public void setExtra(String extra) {
        String[] data = extra.split("\\|");
        direction = BlockFace.valueOf(data[0]);
        upgraded = Boolean.parseBoolean(data[1]);
    }

    public boolean isUpgraded() {
        return upgraded;
    }

    @Override
    public int insertItems(ItemStack stack, BlockFace intoSide) {
        if(stack == null)
            return 0;
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] == null) {
                contents[i] = stack;
                return stack.getAmount();
            } else if (contents[i].isSimilar(stack)) {
                int maxAmountToPush = contents[i].getType().getMaxStackSize() - contents[i].getAmount();
                if (maxAmountToPush == 0)
                    continue;

                int amountToPush = Math.min(stack.getAmount(), maxAmountToPush);
                if (stack.getAmount() - amountToPush == 0) {
                    contents[i] = stack;
                } else {
                    contents[i] = new ItemBuilder(contents[i]).setCount(contents[i].getAmount() + amountToPush);
                }
                return amountToPush;
            }
        }
        return 0;
    }

    @Override
    public ItemStack extractItems(BlockFace fromSide, ItemStack filter) {
        return null;
    }
}
