package de.timeout.libs.gui.events;

import de.timeout.libs.gui.GUI;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

/**
 * Event that will be emitted when a Player closes a GUI
 *
 * @author Timeout
 */
public class GUICloseEvent extends InventoryCloseEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final GUI gui;

    private boolean cancel;

    public GUICloseEvent(@NotNull InventoryView transaction, @NotNull GUI gui) {
        super(transaction);

        this.gui = gui;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return getHandlerList();
    }

    /**
     * Checks if the event is cancelled. <br>
     * Cancelled means, that the close function will not be executed. <br>
     * The Inventory will still close
     *
     * @return true if the event is cancelled, false otherwise
     */
    @Override
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * Cancels the event. <br>
     * Cancelled means, that the close function will not be executed. <br>
     * The Inventory will still close
     *
     * @param b true if the event should be cancelled, false otherwise
     */
    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }

    /**
     * Returns the involved GUI
     *
     * @return the involved GUI
     */
    @NotNull
    public GUI getGUI() {
        return gui;
    }
}
