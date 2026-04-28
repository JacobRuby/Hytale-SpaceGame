package dev.jacobruby.hytale.spacegame.command;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.commands.block.SimpleBlockCommand;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import dev.jacobruby.hytale.spacegame.util.ComponentUtil;
import org.jspecify.annotations.NonNull;

import java.util.stream.Collectors;

public class BlockComponentCommand extends AbstractCommandCollection {

    private static final Message NO_TARGET_BLOCK = Message.raw("No target block found. Try looking at it harder!");


    public BlockComponentCommand() {
        super("blockcomponent", "server.commands.entity.count.desc");

//        this.addSubCommand(new SpawnEmptyEntity());
        this.addSubCommand(new ListCommand());
        this.addSubCommand(new PropCommand());
    }

//    private static class SpawnEmptyEntity extends AbstractPlayerCommand {
//
//        public SpawnEmptyEntity() {
//            super("spawnemptyentity", "");
//        }
//
//        @Override
//        protected void execute(@NonNull CommandContext context, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
//            TransformComponent playerTransform = store.getComponent(ref, TransformComponent.getComponentType());
//
//            assert playerTransform != null;
//
//            Model model = Model.createUnitScaleModel(ModelAsset.DEBUG);
//
//            Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
//            holder.addComponent(TransformComponent.getComponentType(), playerTransform.clone());
//            holder.addComponent(PersistentModel.getComponentType(), new PersistentModel(model.toReference()));
//            holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(model));
//            holder.addComponent(PropComponent.getComponentType(), new PropComponent());
//            holder.addComponent(PropInfoComponent.getComponentType(), new PropInfoComponent());
//            holder.addComponent(PrefabCopyableComponent.getComponentType(), new PrefabCopyableComponent());
//            holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
//            //holder.ensureComponent(HiddenFromAdventurePlayers.getComponentType());
//            UUIDComponent uuidComponent = holder.ensureAndGetComponent(UUIDComponent.getComponentType());
//            String uuidString = uuidComponent.getUuid().toString();
//            holder.addComponent(Nameplate.getComponentType(), new Nameplate(uuidString));
//
//            Ref<EntityStore> entityRef = store.addEntity(holder, AddReason.SPAWN);
//            if (entityRef.isValid()) {
//                context.sendMessage(Message.raw("Successfully spawned empty entity with uuid '{uuid}'.").param("uuid", uuidString));
//            } else {
//                context.sendMessage(Message.raw("Failed to spawn empty entity."));
//            }
//
//        }
//    }

    private static class ListCommand extends SimpleBlockCommand {

        public ListCommand() {
            super("list", "");
        }


        @Override
        protected void executeWithBlock(@NonNull CommandContext context, @NonNull WorldChunk chunk, int x, int y, int z) {
            Ref<ChunkStore> blockRef = chunk.getBlockComponentEntity(x, y, z);
            if (blockRef == null || !blockRef.isValid()) {
                context.sendMessage(NO_TARGET_BLOCK);
                return;
            }

            var componentTypes = ComponentUtil.getComponentTypes(blockRef);

            context.sendMessage(Message.raw("The target block at %s, %s, %s has the following components:".formatted(x, y, z)));
            context.sendMessage(Message.raw(componentTypes.stream().map(ComponentType::getTypeClass).map(Class::getSimpleName).collect(Collectors.joining(", "))));
        }
    }

    private static class PropCommand extends AbstractCommandCollection {

        public PropCommand() {
            super("prop", "");

            this.addSubCommand(new PathCommand());
//            this.addSubCommand(new AddCommand());
        }

        private static class PathCommand extends SimpleBlockCommand {

            private final OptionalArg<String> setArg = this.withOptionalArg("set", "", ArgTypes.STRING);

            public PathCommand() {
                super("path", "");
            }

            @Override
            protected void executeWithBlock(@NonNull CommandContext context, @NonNull WorldChunk chunk, int x, int y, int z) {
                Ref<ChunkStore> blockRef = chunk.getBlockComponentEntity(x, y, z);
                if (blockRef == null || !blockRef.isValid()) {
                    context.sendMessage(NO_TARGET_BLOCK);
                    return;
                }

//                Store<ChunkStore> store = blockRef.getStore();
//                var propInfo = store.getComponent(blockRef, PropInfoBlockComponent.getComponentType());
//
//                if (propInfo == null) {
//                    context.sendMessage(Message.raw("Target block does not have a PropInfoBlockComponent."));
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

//        private static class AddCommand extends SimpleBlockCommand {
//
//            public AddCommand() {
//                super("path", "");
//            }
//
//            @Override
//            protected void executeWithBlock(@NonNull CommandContext context, @NonNull WorldChunk chunk, int x, int y, int z) {
//                Ref<ChunkStore> blockRef = chunk.getBlockComponentEntity(x, y, z);
//                if (blockRef == null || !blockRef.isValid()) {
//                    context.sendMessage(NO_TARGET_BLOCK);
//                    return;
//                }
//
//                Store<ChunkStore> store = blockRef.getStore();
//                var propInfo = store.getComponent(blockRef, PropInfoBlockComponent.getComponentType());
//
//                if (propInfo != null) {
//                    context.sendMessage(Message.raw("Target block already has a PropInfoBlockComponent."));
//                    return;
//                }
//
//                store.ensureComponent(blockRef, PropInfoBlockComponent.getComponentType());
//
//                if (set != null) {
//                    propInfo.setPropPath(set);
//                    context.sendMessage(Message.raw("Set PropPath to %s".formatted(set)));
//                } else {
//                    context.sendMessage(Message.raw("PropPath: %s".formatted(propInfo.getPropPath())));
//                }
//
//            }
//        }
    }
}
