package dev.bigstacks;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public final class BigStacksPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        int configured = getConfig().getInt("max-stack-size", 99);
        if (configured > 99 || configured < 1) {
            getLogger().warning("max-stack-size muss zwischen 1 und 99 liegen (Minecraft-Limit). Verwende "
                    + Math.max(1, Math.min(99, configured)) + ".");
        }
        StackNormalizer normalizer = new StackNormalizer(configured);
        boolean autoPickup = getConfig().getBoolean("auto-pickup", true);
        long interval = Math.max(1L, getConfig().getLong("normalize-interval-ticks", 20L));

        getServer().getPluginManager().registerEvents(new BigStacksListener(normalizer, autoPickup), this);

        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Player player : getServer().getOnlinePlayers()) {
                normalizer.normalizeInventory(player.getInventory());
                Inventory top = player.getOpenInventory().getTopInventory();
                if (isPlainContainer(top.getType())) {
                    normalizer.normalizeInventory(top);
                }
            }
        }, interval, interval);

        getLogger().info("BigStacks aktiv: Stackgroesse " + normalizer.getTarget()
                + ", Auto-Pickup " + (autoPickup ? "an" : "aus"));
    }

    /** Nur reine Lager-Container, keine Inventare mit Ergebnis-Slots. */
    private static boolean isPlainContainer(InventoryType type) {
        return switch (type) {
            case CHEST, BARREL, SHULKER_BOX, ENDER_CHEST, HOPPER, DISPENSER, DROPPER -> true;
            default -> false;
        };
    }
}
