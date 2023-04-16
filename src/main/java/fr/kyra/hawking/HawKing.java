package fr.kyra.hawking;


import fr.kyra.hawking.handler.ServerStopHandler;
import fr.kyra.hawking.listeners.BlocksListener;
import fr.kyra.hawking.manager.DatabaseManager;
import fr.kyra.hawking.manager.RequestManager;
import fr.kyra.hawking.objects.CustomPlayer;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class HawKing extends JavaPlugin {

    private static HawKing instance;
    private DatabaseManager databaseManager;
    private final HashMap<UUID, CustomPlayer> customPlayers = new HashMap<>();
    private ArrayList<Material> authorizedBlocks = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        this.databaseManager = new DatabaseManager(this.getConfig());

        if (databaseManager.getConnection() == null) {
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        try {
            Runtime.getRuntime().addShutdownHook(new ServerStopHandler());
            this.getServer().getPluginManager().registerEvents(new BlocksListener(), this);
            this.authorizedBlocks = RequestManager.getAuthorizedBlocks();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onDisable() {}

    public static HawKing getInstance() {
        return instance;
    }

    public HashMap<UUID, CustomPlayer> getCustomPlayers() {
        return customPlayers;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public ArrayList<Material> getAuthorizedBlocks() {
        return authorizedBlocks;
    }
}
