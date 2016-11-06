package me.yamakaja.devathon2016.machine;

import me.yamakaja.devathon2016.DevathonPlugin;
import me.yamakaja.devathon2016.electricity.ElectricityNetwork;
import me.yamakaja.devathon2016.electricity.IElectricityReceiver;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;

/**
 * Created by Yamakaja on 06.11.16.
 */
public class MachineQuarry extends Machine implements IElectricityReceiver {

    private ElectricityNetwork electricityNetwork;
    private int x,y,z;
    private int buffer = 0;

    private Location dropLocation;

    public static final int MAX_BUFFER = 100000;

    @Override
    public MachineType getType() {
        return MachineType.QUARRY;
    }

    @Override
    public void update() {
        if(y == 0) {
            getLocation().getWorld().spawnParticle(Particle.BARRIER, dropLocation, 1, 0, 0, 0, 0);
            return;
        }

        if(x >= 16) {
            x = 0;
            if(++z >= 16) {
                z = 0;
                y--;
            }
        }

        Block block = getLocation().getChunk().getBlock(x++, y, z);

        if(block == null)
            return;

        block.getLocation().getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK.getId());

        if(block.getType().toString().contains("ORE")){
            block.getDrops().forEach(item -> dropLocation.getWorld().dropItem(dropLocation, item));
            block.setType(Material.AIR);
        }
    }

    @Override
    public void onBreak(Player player) {
        if (electricityNetwork != null) {
            electricityNetwork.unregister(this);
            return;
        }
        System.out.println("Furnace had null-network!");
    }

    @Override
    public void onInteract(Player player) {
        player.sendMessage(ChatColor.GOLD + "[Quarry] " + ChatColor.AQUA + "Currently mining at: " + x + "," + y + "," + z);
    }

    @Override
    public ElectricityNetwork getNetwork() {
        return electricityNetwork;
    }

    @Override
    public int getBuffer() {
        return buffer;
    }

    @Override
    public void setBuffer(int amount) {
        buffer = amount;
    }

    @Override
    public int getDemand() {
        return MAX_BUFFER - buffer;
    }

    @Override
    public int acceptEnergy(int amount) {
        int max = MAX_BUFFER - buffer;
        int actualAmount = Math.min(max, amount);
        buffer += actualAmount;
        return actualAmount;
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

    @Override
    public String getExtra() {
        return x + "|" + y + "|" + z;
    }

    @Override
    public void setExtra(String extra) {
        String[] data = extra.split("\\|");
        x = Integer.parseInt(data[0]);
        y = Integer.parseInt(data[1]);
        z = Integer.parseInt(data[2]);
    }

    @Override
    public void setLocation(Location location) {
        super.setLocation(location);
        y = location.getBlockY();
        this.dropLocation = getLocation().clone().add(0.5, 1.5, 0.5);
    }
}
