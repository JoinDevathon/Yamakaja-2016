package me.yamakaja.devathon2016;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.yamakaja.devathon2016.config.MachinesConfig;
import me.yamakaja.devathon2016.electricity.ElectricityManager;
import me.yamakaja.devathon2016.util.gson.ItemStackTypeAdapter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class DevathonPlugin extends JavaPlugin {

    private static DevathonPlugin instance;
    private GsonBuilder gsonBuilder = new GsonBuilder();
    private MachinesConfig machinesConfig;
    private MachineManager machineManager;
    private ElectricityManager electricityManager;

    public static DevathonPlugin getInstance() {
        if (instance == null)
            throw new RuntimeException("Misplaced call to DevathonPlugin#getInstance()!");

        return instance;
    }

    public ElectricityManager getElectricityManager() {
        return electricityManager;
    }

    @Override
    public void onEnable() {
        instance = this;
        gsonBuilder.registerTypeHierarchyAdapter(ItemStack.class, new ItemStackTypeAdapter());

        if (!loadConfigs())
            throw new RuntimeException("Failed to load configs!");

        machineManager = new MachineManager(this);
        electricityManager = new ElectricityManager(this);

        new ItemDebugTool(this);
        new TickLoop(this);

    }

    public MachineManager getMachineManager() {
        return machineManager;
    }

    @Override
    public void onDisable() {
        machineManager.saveMachines();

    }

    public MachinesConfig getMachinesConfig() {
        return machinesConfig;
    }

    public GsonBuilder getGsonBuilder() {
        return gsonBuilder;
    }

    private boolean loadConfigs() {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        return loadMachineConfig();

    }

    private boolean loadMachineConfig() {
        Gson gson = gsonBuilder.create();
        File configFile = new File(this.getDataFolder(), "machines.json");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();

                InputStream inputStream = this.getResource("machines.json");
                OutputStream outputStream = new FileOutputStream(configFile);
                byte[] buffer = new byte[1024];
                int count;
                while ((count = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, count);
                    System.out.println("Writing ...");
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(configFile));
            machinesConfig = gson.fromJson(inputStreamReader, MachinesConfig.class);
            inputStreamReader.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}

