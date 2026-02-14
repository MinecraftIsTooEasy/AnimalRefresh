package com.inf1nlty.animalrefresh.mixin;

import com.inf1nlty.animalrefresh.IChunkProviderServerBridge;
import com.inf1nlty.animalrefresh.util.AnimalRefreshController;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(ChunkProviderServer.class)
@SuppressWarnings("rawtypes")
@Implements(@Interface(iface = IChunkProviderServerBridge.class, prefix = "ar$"))
public abstract class IChunkProviderServerMixin implements IChunkProviderServerBridge {

    @Shadow private List loadedChunks;

    @Shadow public abstract boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate);

    @Unique
    private int countAnimalsInChunk(Chunk chunk) {
        if (chunk == null || chunk.worldObj == null) return 0;
        World world = chunk.worldObj;
        int cx = chunk.xPosition;
        int cz = chunk.zPosition;
        int count = 0;

        List worldEntities = world.loadedEntityList;
        if (worldEntities == null) return 0;

        for (Object object : worldEntities) {
            if (!(object instanceof Entity entity)) continue;
            if (!(entity instanceof EntityAnimal)) continue;

            int ex = MathHelper.floor_double(entity.posX) >> 4;
            int ez = MathHelper.floor_double(entity.posZ) >> 4;
            if (ex == cx && ez == cz) {
                ++count;
            }
        }
        return count;
    }

    @Unique
    public int ar$resetAllLoadedChunksAnimalSpawnFlags() {
        int changed = 0;
        int totalSpawned = 0;

        if (this.loadedChunks == null) {
            return 0;
        }

        int printed = 0;
        for (Object object : this.loadedChunks) {
            if (!(object instanceof Chunk chunk)) continue;

            if (printed < 20) {
                System.out.println("[AnimalRefresh][DEBUG] resetting chunk " + chunk.xPosition + "," + chunk.zPosition
                        + " old_animals_spawned=" + chunk.animals_spawned + " old_isTerrainPopulated=" + chunk.isTerrainPopulated);
                printed++;
            }

            int before = countAnimalsInChunk(chunk);

            chunk.animals_spawned = 0;
            chunk.setChunkModified();

            try {
                World world = chunk.worldObj;
                int minX = chunk.getMinBlockX();
                int minZ = chunk.getMinBlockZ();
                BiomeGenBase biome = world.getBiomeGenForCoords(minX + 8, minZ + 8);

                SpawnerAnimals.performWorldGenSpawning(world, biome, EnumCreatureType.animal, minX, minZ, 16, 16, world.rand);
            } catch (Throwable t) {
                System.out.println("[AnimalRefresh][ERROR] exception while spawning animals for chunk " + chunk.xPosition + "," + chunk.zPosition + ": " + t);
            }

            int after = countAnimalsInChunk(chunk);
            int delta = after - before;
            if (delta > 0) totalSpawned += delta;

            ++changed;
        }

        this.saveChunks(true, null);

        // accumulate (do not overwrite) the global counter so multiple providers' results sum up
        AnimalRefreshController.lastSpawnedAnimals += totalSpawned;

        System.out.println("[AnimalRefresh][DEBUG] total changed in mixin: " + changed + " total animals spawned (this provider): " + totalSpawned);
        return changed;
    }
}