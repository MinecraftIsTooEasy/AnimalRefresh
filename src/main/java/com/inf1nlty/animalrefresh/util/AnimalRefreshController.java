package com.inf1nlty.animalrefresh.util;

import com.inf1nlty.animalrefresh.IChunkProviderServerBridge;
import net.minecraft.IChunkProvider;
import net.minecraft.WorldServer;
import net.minecraft.server.MinecraftServer;

public final class AnimalRefreshController {

    public static int lastSpawnedAnimals = 0;

    private AnimalRefreshController() {}

    public static int resetAllLoadedChunksAnimalSpawnFlags() {
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) return 0;

        // clear lastSpawnedAnimals before operation
        lastSpawnedAnimals = 0;

        int totalChanged = 0;
        WorldServer[] worlds = server.worldServers;
        if (worlds == null) return 0;

        for (WorldServer world : worlds) {
            if (world == null) continue;
            IChunkProvider provider = world.getChunkProvider();
            if (provider instanceof IChunkProviderServerBridge) {
                totalChanged += ((IChunkProviderServerBridge) provider).resetAllLoadedChunksAnimalSpawnFlags();
            }
        }

        System.out.println("[AnimalRefresh] resetAllLoadedChunksAnimalSpawnFlags -> total chunks reset: " + totalChanged + " total animals spawned: " + lastSpawnedAnimals);
        return totalChanged;
    }
}