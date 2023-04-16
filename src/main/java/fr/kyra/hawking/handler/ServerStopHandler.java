package fr.kyra.hawking.handler;

import fr.kyra.hawking.HawKing;
import fr.kyra.hawking.manager.RequestManager;
import fr.kyra.hawking.objects.CustomPlayer;

import java.util.HashMap;
import java.util.UUID;

public class ServerStopHandler extends Thread {

    public void run() {
        HashMap<UUID, CustomPlayer> customPlayerHashMap = HawKing.getInstance().getCustomPlayers();
        customPlayerHashMap.forEach(RequestManager::updatePlayer);
    }
}
