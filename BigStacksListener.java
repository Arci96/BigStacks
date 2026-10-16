package dev.bigstacks;

import org.bukkit.GameMode;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.Map;

public final class BigStacksListener implements Listener {

    private final StackNormalizer normalizer;
    private final boolean autoPickup;

    public BigStacksListener(StackNormalizer normalizer, boolean autoPickup) {
        this.normalizer = normalizer;
        this.autoPickup = autoPickup;
    }

    /** Abgebaute Bloecke direkt ins Inventar. */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockDrop(BlockDropItemEvent event) {
        if (!autoPickup) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.hasPermission("bigstacks.autopickup")) {
            return;
        }
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        normalizer.normalizeInventory(player.getInventory());

        Iterator<Item> it = event.getItems().iterator();
        while (it.hasNext()) {
            Item entity = it.next();
            ItemStack stack = entity.getItemStack().clone();
            normalizer.apply(stack);

            Map<Integer, ItemStack> leftover = player.getInventory().addItem(stack);
            if (leftover.isEmpty()) {
                it.remove(); // komplett eingesammelt
            } else {
                // Rest faellt normal auf den Boden
                entity.setItemStack(leftover.values().iterator().next());
            }
        }
    }

    /** Jedes neu gespawnte Item-Entity (Drops, weggeworfene Items) bekommt die neue Stackgroesse. */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemSpawn(ItemSpawnEvent event) {
        Item entity = event.getEntity();
        ItemStack stack = entity.getItemStack();
        if (normalizer.apply(stack)) {
            entity.setItemStack(stack);
        }
    }

    /** Crafting-Ergebnisse. */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        if (result != null && normalizer.apply(result)) {
            event.getInventory().setResult(result);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        normalizer.normalizeInventory(event.getPlayer().getInventory());
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        normalizer.normalizeInventory(event.getInventory());
        normalizer.normalizeInventory(event.getPlayer().getInventory());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        normalizer.normalizeInventory(event.getInventory());
        normalizer.normalizeInventory(event.getPlayer().getInventory());
    }
}
