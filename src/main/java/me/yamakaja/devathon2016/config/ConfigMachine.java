package me.yamakaja.devathon2016.config;

import me.yamakaja.devathon2016.machine.MachineType;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Yamakaja on 05.11.16.
 */
public class ConfigMachine {

    public MachineType type;
    public int x,y,z;
    public String owner;

    public ItemStack[] contents;
    
    public int storedElectricity;

    public String extra;

    public ConfigMachine(MachineType type, int x, int y, int z, ItemStack[] contents, String owner, int storedElectricity) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.contents = contents;
        this.owner = owner;
        this.storedElectricity = storedElectricity;
    }

    public ConfigMachine(MachineType type, int x, int y, int z, String owner, ItemStack[] contents, String extra) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.owner = owner;
        this.contents = contents;
        this.extra = extra;
    }

    public ConfigMachine () {

    }

    public ConfigMachine setType(MachineType type) {
        this.type = type;
        return this;
    }

    public ConfigMachine setX(int x) {
        this.x = x;
        return this;
    }

    public ConfigMachine setY(int y) {
        this.y = y;
        return this;
    }

    public ConfigMachine setZ(int z) {
        this.z = z;
        return this;
    }

    public ConfigMachine setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public ConfigMachine setContents(ItemStack[] contents) {
        this.contents = contents;
        return this;
    }

    public ConfigMachine setStoredElectricity(int storedElectricity) {
        this.storedElectricity = storedElectricity;
        return this;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
