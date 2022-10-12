package de.rytrox.spicy;

import de.rytrox.spicy.config.JsonConfig;
import de.rytrox.spicy.config.UTFConfig;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class SpicyTestPlugin extends JavaPlugin {

    private UTFConfig utfConfig;
    private JsonConfig jsonConfig;

    public SpicyTestPlugin() {
        super();
    }

    protected SpicyTestPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public @NotNull UTFConfig getConfig() {
        return utfConfig;
    }

    @Override
    public void reloadConfig() {
        utfConfig = new UTFConfig(new File(getDataFolder(), "config.yml"));
        jsonConfig = new JsonConfig(new File(getDataFolder(), "config.json"));
    }

    @Override
    public void saveConfig() {
        try {
            utfConfig.save(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            getLogger().log(Level.WARNING, e, () -> "&cUnable to save config.yml");
        }
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {

    }

    public void saveJsonConfig() {
        try {
            jsonConfig.save(new File(getDataFolder(), "config.json"));
        } catch (IOException e) {
            getLogger().log(Level.WARNING, e, () -> "&cUnable to save config.json");
        }
    }
}
