package dev.jacobruby.hytale.spacegame.command;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.types.EntityWrappedArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jacobruby.hytale.spacegame.util.ComponentUtil;
import dev.jacobruby.hytale.spacegame.util.GenUtil;

import javax.annotation.Nonnull;

import java.util.stream.Collectors;

public class ComponentCommand extends AbstractCommandCollection {

    private static final Message NO_TARGET_ENTITY = Message.raw("No target entity found. Try looking at it harder!");


    public ComponentCommand() {
        super("component", "server.commands.entity.count.desc");

        this.addSubCommand(new SpawnEmptyEntity());
        this.addSubCommand(new ListCommand());
        this.addSubCommand(new PropCommand());
    }

    private static class SpawnEmptyEntity extends AbstractPlayerCommand {

        public SpawnEmptyEntity() {
            super("spawnemptyentity", "");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            TransformComponent playerTransform = store.getComponent(ref, TransformComponent.getComponentType());

            assert playerTransform != null;

            Ref<EntityStore> entityRef = GenUtil.spawnEmptyPrefabMarker(playerTransform.clone(), store, null);
            if (entityRef == null || !entityRef.isValid()) {
                context.sendMessage(Message.raw("Failed to spawn empty entity."));
            } else {
                context.sendMessage(Message.raw("Successfully spawned empty entity."));
            }
        }
    }

    private static class ListCommand extends AbstractPlayerCommand {

        private final EntityWrappedArg entityArg = this.withOptionalArg("entity", "", ArgTypes.ENTITY_ID);

        public ListCommand() {
            super("list", "");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            Ref<EntityStore> entityRef = this.entityArg.get(store, context);
            if (entityRef == null || !entityRef.isValid()) {
                context.sendMessage(NO_TARGET_ENTITY);
                return;
            }

            UUIDComponent uuidComponent = store.getComponent(entityRef, UUIDComponent.getComponentType());

            assert uuidComponent != null;

            var componentTypes = ComponentUtil.getComponentTypes(entityRef);

            context.sendMessage(Message.raw("The target entity with uuid %s has the following components:".formatted(uuidComponent.getUuid())));
            context.sendMessage(Message.raw(componentTypes.stream().map(ComponentType::getTypeClass).map(Class::getSimpleName).collect(Collectors.joining(", "))));
        }
    }

    private static class PropCommand extends AbstractCommandCollection {

        public PropCommand() {
            super("prop", "");

            this.addSubCommand(new PathCommand());
        }

        private static class PathCommand extends AbstractPlayerCommand {

            private final OptionalArg<String> setArg = this.withOptionalArg("set", "", ArgTypes.STRING);
            private final EntityWrappedArg entityArg = this.withOptionalArg("entity", "", ArgTypes.ENTITY_ID);

            public PathCommand() {
                super("path", "");
            }

            @Override
            protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
                Ref<EntityStore> entityRef = this.entityArg.get(store, context);
                if (entityRef == null || !entityRef.isValid()) {
                    context.sendMessage(NO_TARGET_ENTITY);
                    return;
                }

//                var propInfo = store.getComponent(entityRef, PropInfoComponent.getComponentType());
//
//                if (propInfo == null) {
//                    context.sendMessage(Message.raw("Target entity does not have a PropInfoComponent."));
//                    return;
//                }
//
//                String set = this.setArg.get(context);
//
//                if (set != null) {
//                    propInfo.setPropPath(set);
//                    context.sendMessage(Message.raw("Set PropPath to %s".formatted(set)));
//                } else {
//                    context.sendMessage(Message.raw("PropPath: %s".formatted(propInfo.getPropPath())));
//                }
            }
        }
    }
}
