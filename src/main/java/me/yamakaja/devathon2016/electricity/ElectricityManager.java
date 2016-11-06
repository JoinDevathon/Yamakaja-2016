package me.yamakaja.devathon2016.electricity;

import me.yamakaja.devathon2016.DevathonPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yamakaja on 05.11.16.
 */
public class ElectricityManager {

    private DevathonPlugin plugin;
    private Map<String, ElectricityNetwork> networkMap = new HashMap<>();

    public ElectricityManager(DevathonPlugin plugin) {
        this.plugin = plugin;

        new BukkitRunnable(){
            @Override
            public void run() {

            }
        }.runTaskTimer(plugin, 0, 5);
    }

    public ElectricityNetwork getNetwork(String name) {
        ElectricityNetwork network = networkMap.get(name);
        if(network == null) {
            network = new ElectricityNetwork();
            networkMap.put(name, network);
        }
        return network;
    }

    public void updateNetworks() {
        networkMap.values().forEach(ElectricityNetwork::update);
    }

}
