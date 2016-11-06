package me.yamakaja.devathon2016;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Yamakaja on 05.11.16.
 */
public class TickLoop extends BukkitRunnable {

    private DevathonPlugin plugin;

    public TickLoop(DevathonPlugin plugin) {
        this.plugin = plugin;
        this.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public void run() {
        plugin.getMachineManager().updateMachines();
        plugin.getElectricityManager().updateNetworks();
    }
}
