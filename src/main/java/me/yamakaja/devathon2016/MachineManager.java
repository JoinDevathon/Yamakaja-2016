package me.yamakaja.devathon2016;

import com.google.gson.Gson;
import me.yamakaja.devathon2016.config.ConfigMachine;
import me.yamakaja.devathon2016.config.CraftingRecipe;
import me.yamakaja.devathon2016.config.WorldSettings;
import me.yamakaja.devathon2016.electricity.IElectricMachine;
import me.yamakaja.devathon2016.inventory.IInventory;
import me.yamakaja.devathon2016.machine.Machine;
import me.yamakaja.devathon2016.machine.MachineType;
import me.yamakaja.devathon2016.util.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ShapedRecipe;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Yamakaja on 05.11.16.
 */
public class MachineManager implements Listener {

    private final Map<String, ArrayList<Machine>> machines;
    private DevathonPlugin plugin;

    public MachineManager(DevathonPlugin plugin) {
        this.plugin = plugin;
        this.machines = new HashMap<>();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        registerRecipes();
    }

    public void updateMachines() {
        machines.values().forEach(machines -> machines.forEach(Machine::update));
    }

    private void registerRecipes() {

        ShapedRecipe shapedRecipe;
        for (CraftingRecipe craftingRecipe : plugin.getMachinesConfig().recipes) {

            shapedRecipe = new ShapedRecipe(new ItemBuilder(craftingRecipe.machineType.getBlockType(), 1)
                    .setDisplayName(craftingRecipe.machineType.getName()).setLore(craftingRecipe.machineType.getLore()))
                    .shape(craftingRecipe.shape);

            for (CraftingRecipe.Ingredient ingredient : craftingRecipe.ingredients) {
                shapedRecipe.setIngredient(ingredient.symbol.charAt(0), ingredient.materialType);
            }

            plugin.getServer().addRecipe(shapedRecipe);

        }

    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        File machinesFile = new File(plugin.getDataFolder(), "data/" + event.getWorld().getName() + ".json");
        this.machines.put(event.getWorld().getName(), new ArrayList<>());

        if (!machinesFile.exists())
            return;

        Gson gson = plugin.getGsonBuilder().create();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(machinesFile));
            Arrays.stream(gson.fromJson(inputStreamReader, WorldSettings.class).machines).forEach(configMachine -> {
                try {
                    Machine machine = configMachine.type.getMachineClass().newInstance();
                    machine.setLocation(new Location(event.getWorld(), configMachine.x, configMachine.y, configMachine.z));

                    machine.setOwner(configMachine.owner);
                    machine.setExtra(configMachine.extra);

                    if (machine instanceof IInventory) {
                        ((IInventory) machine).setContents(configMachine.contents);
                    }

                    if (machine instanceof IElectricMachine) {
                        ((IElectricMachine) machine).setBuffer(configMachine.storedElectricity);
                    }

                    machines.get(event.getWorld().getName()).add(machine);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveMachines() {
        Map<String, WorldSettings> worldSettingsMap = plugin.getServer().getWorlds().stream()
                .collect(Collectors.toMap(World::getName, world -> new WorldSettings(new ConfigMachine[getMachines().get(world.getName()).size()])));

        getMachines().forEach((world, machines) -> {
            WorldSettings settings = worldSettingsMap.get(world);
            for (int i = 0; i < machines.size(); i++) {
                settings.machines[i] = new ConfigMachine(machines.get(i).getType(), machines.get(i).getLocation().getBlockX(),
                        machines.get(i).getLocation().getBlockY(), machines.get(i).getLocation().getBlockZ(),
                        machines.get(i).getOwner(), (machines.get(i) instanceof IInventory ? ((IInventory) machines.get(i)).getInventory()
                                : null), machines.get(i).getExtra());
                if (machines.get(i) instanceof IElectricMachine) {
                    settings.machines[i].setStoredElectricity(((IElectricMachine) machines.get(i)).getBuffer());
                }
            }
        });

        File worldData = new File(plugin.getDataFolder(), "data");
        if (!worldData.exists())
            worldData.mkdir();

        Gson gson = plugin.getGsonBuilder().create();
        for (Map.Entry<String, WorldSettings> entry : worldSettingsMap.entrySet()) {
            try {
                OutputStream outputStream = new FileOutputStream(new File(worldData, entry.getKey() + ".json"));
                outputStream.write(gson.toJson(entry.getValue()).getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        MachineType type = MachineType.getMachineTypeForItemStack(event.getItemInHand());

        if (type == null)
            return;

        try {
            Machine machine = type.getMachineClass().newInstance();
            machine.setLocation(event.getBlock().getLocation());
            machines.get(event.getBlock().getLocation().getWorld().getName()).add(machine);
            machine.onPlace(event.getPlayer());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Machine machine = getMachineAt(event.getBlock().getLocation());
        if (machine == null)
            return;
        event.setCancelled(true);
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemBuilder(machine.getType()
                    .getBlockType()).setDisplayName(machine.getType().getName()).setLore(machine.getType().getLore()));
        machine.onBreak(event.getPlayer());
        event.getBlock().setType(Material.AIR);
        machines.get(event.getBlock().getLocation().getWorld().getName()).remove(machine);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getPlayer().isSneaking() || e.getHand() == EquipmentSlot.OFF_HAND)
            return;

        Machine machine = getMachineAt(e.getClickedBlock().getLocation());
        if (machine == null)
            return;

        e.setCancelled(true);

        machine.onInteract(e.getPlayer());
    }

    public Machine getMachineAt(Location location) {
        for (Machine machine : machines.get(location.getWorld().getName())) {
            if (machine.getLocation().equals(location))
                return machine;
        }

        return null;
    }

    public Map<String, ArrayList<Machine>> getMachines() {
        return machines;
    }
}
