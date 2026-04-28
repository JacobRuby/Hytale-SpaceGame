package dev.jacobruby.hytale.spacegame.util;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

public final class ComponentUtil {

    private ComponentUtil() {
    }

    public static <ECS_TYPE> List<ComponentType<ECS_TYPE, Component<ECS_TYPE>>> getComponentTypes(@Nonnull Ref<ECS_TYPE> ref) {
        var archetype = ref.getStore().getArchetype(ref);
        List<ComponentType<ECS_TYPE, Component<ECS_TYPE>>> componentTypes = new ArrayList<>();

        for (int i = archetype.getMinIndex(); i < archetype.length(); i++) {
            var componentType = archetype.get(i);

            if (componentType != null) {
                //noinspection unchecked
                componentTypes.add((ComponentType<ECS_TYPE, Component<ECS_TYPE>>) componentType);
            }
        }

        return componentTypes;
    }



}
