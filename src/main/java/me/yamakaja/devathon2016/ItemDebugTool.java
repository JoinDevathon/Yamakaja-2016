package me.yamakaja.devathon2016;

import me.yamakaja.devathon2016.electricity.IElectricMachine;
import me.yamakaja.devathon2016.electricity.IElectricityProvider;
import me.yamakaja.devathon2016.electricity.IElectricityReceiver;
import me.yamakaja.devathon2016.inventory.IInventory;
import me.yamakaja.devathon2016.machine.Machine;
import me.yamakaja.devathon2016.machine.MachinePipe;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Yamakaja on 05.11.16.
 */
public class ItemDebugTool implements Listener {

    private DevathonPlugin plugin;

    public ItemDebugTool(DevathonPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.getPlayer().isOp()
                || event.getItem() == null || event.getItem().getType() != Material.BLAZE_ROD)
            return;

        Machine machine = plugin.getMachineManager().getMachineAt(event.getClickedBlock().getLocation());
        if (machine == null)
            return;

        event.getPlayer().sendMessage("Owner: " + machine.getOwner());

        if (machine instanceof IInventory) {
            event.getPlayer().sendMessage("Inventory present!");
        }

        if (machine instanceof IElectricMachine) {
            event.getPlayer().sendMessage("Currently stored energy: " + ((IElectricMachine) machine).getBuffer());

            if (machine instanceof IElectricityProvider) {
                event.getPlayer().sendMessage("Electricity provider present!");
            }

            if (machine instanceof IElectricityReceiver) {
                event.getPlayer().sendMessage("Electricity receiver present!");
            }
        }

        if (machine instanceof MachinePipe){
            event.getPlayer().sendMessage("Pushes " + ((MachinePipe) machine).getDirection());
            if(((MachinePipe) machine).isUpgraded())
                event.getPlayer().sendMessage("Upgraded!");
        }
    }
}
