package dev.jacobruby.hytale.spacegame.incursion.generation.poi;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.prefab.PrefabLoadException;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockMask;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.world.World;
import dev.jacobruby.hytale.spacegame.Core;
import dev.jacobruby.hytale.spacegame.asset.MarkerType;

import java.nio.file.Path;

public class POI {

    private final String prefabPath;
    private MarkerType markerType;

    public POI(String prefabPath, MarkerType markerType) {
        this.prefabPath = prefabPath;
        this.markerType = markerType;
    }

    public void generate(World world, Vector3d origin) {
        BlockSelection prefab;

        try {
            PrefabStore prefabStore = PrefabStore.get();
            Path path = prefabStore.findAssetPrefabPath(this.prefabPath);

            if (path != null) {
                prefab = prefabStore.getPrefab(path);
            } else {
                Core.warn().log("Prefab not found for '%s'", this.prefabPath);
                return;
            }
        } catch (PrefabLoadException e) {
            e.printStackTrace();
            return;
        }

        if (prefab == null) {
            Core.warn().log("Prefab was null!");
            return;
        }

        prefab.place(ConsoleSender.INSTANCE, world, origin.toVector3i(), BlockMask.EMPTY);
        Core.info().log("Pasted %s", this.prefabPath);
    }

    public String getPrefabPath() {
        return prefabPath;
    }

    public MarkerType getMarkerType() {
        return markerType;
    }
}
