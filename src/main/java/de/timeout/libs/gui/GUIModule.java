package de.timeout.libs.gui;

import de.timeout.libs.gui.events.ButtonClickEvent;
import de.timeout.libs.gui.events.GUICloseEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class GUIModule implements Listener {

    private static final GUIModule MODULE = new GUIModule();
    private static final List<Plugin> MODULE_OWNERS = new ArrayList<>();

    private Plugin moduleOwner;

    public static void enableGUIModule(@NotNull JavaPlugin moduleOwner) {
        // Unregister all before enabling
        HandlerList.unregisterAll(MODULE);

        // register events for this plugin
        MODULE.moduleOwner = moduleOwner;
        Bukkit.getPluginManager().registerEvents(MODULE, moduleOwner);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShutDown(@NotNull PluginDisableEvent event) {
        if(event.getPlugin().equals(MODULE.moduleOwner)) {
            MODULE_OWNERS.remove(event.getPlugin());

            // get next owner
            if(!MODULE_OWNERS.isEmpty()) {
                MODULE.moduleOwner = MODULE_OWNERS.get(0);
                Bukkit.getPluginManager().registerEvents(MODULE, MODULE_OWNERS.get(0));
            } else {
                MODULE.moduleOwner = null;
                Bukkit.getLogger().log(Level.INFO, "&aDeactivated GUI-Module successfully!");
            }
        }
    }

    @EventHandler
    public void onGUIClose(@NotNull InventoryCloseEvent event) {
        // get GUI
        GUI gui = GUI.getOpenedGUI(event.getPlayer());

        if(gui != null) {
            GUICloseEvent closeEvent = new GUICloseEvent(event.getView(), gui);

            Bukkit.getPluginManager().callEvent(closeEvent);
            if(!closeEvent.isCancelled()) {
                Optional.ofNullable(closeEvent.getGUI().getCloseFunction())
                        .ifPresent((consumer) -> consumer.accept(closeEvent));
            }
        }
    }

    @EventHandler
    public void onButtonClick(@NotNull InventoryClickEvent event) {
        if(event.getClickedInventory() != null && event.getCurrentItem() != null) {
            // get GUI
            GUI gui = GUI.getOpenedGUI(event.getWhoClicked());

            if(gui != null && !event.isCancelled()) {
                // cancel event (Deny is safer than normal cancel)
                event.setResult(Event.Result.DENY);

                // create click event
                ButtonClickEvent clickEvent = new ButtonClickEvent(event.getView(),
                        event.getSlotType(), event.getSlot(), event.getClick(), event.getAction());
                Bukkit.getPluginManager().callEvent(clickEvent);
                if(!clickEvent.isCancelled()) {
                    Optional.ofNullable(gui.getClickFunction(clickEvent.getSlot()))
                                    .ifPresent((function) -> function.accept(clickEvent));
                }
            }
        }
    }
}
