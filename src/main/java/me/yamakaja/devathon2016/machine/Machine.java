package me.yamakaja.devathon2016.machine;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Yamakaja on 05.11.16.
 */
public abstract class Machine {

    public abstract MachineType getType();
    public abstract void update();
    public String owner;

    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        if(this.location != null) {
            throw new RuntimeException("You cannot re-locate a machine!");
        }
        this.location = location;
    }

    public abstract void onBreak(Player player);

    public  void onPlace(Player player){
        this.owner = player.getUniqueId().toString();
    }

    public abstract void onInteract(Player player);

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getExtra() {
        return null;
    }

    public void setExtra(String extra) {

    }

}
