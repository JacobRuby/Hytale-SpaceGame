package dev.jacobruby.hytale.spacegame.asset.generator;

import com.hypixel.hytale.builtin.hytalegenerator.assets.props.PropAsset;
import com.hypixel.hytale.builtin.hytalegenerator.props.EmptyProp;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import dev.jacobruby.hytale.spacegame.Core;
import dev.jacobruby.hytale.spacegame.asset.MarkerType;
import dev.jacobruby.hytale.spacegame.asset.prop.MarkerProp;

import javax.annotation.Nonnull;

public class MarkerPropAsset extends PropAsset {
    @Nonnull
    public static final BuilderCodec<MarkerPropAsset> CODEC = BuilderCodec.builder(
            MarkerPropAsset.class, MarkerPropAsset::new, PropAsset.ABSTRACT_CODEC
        )
        .append(new KeyedCodec<>("Marker", MarkerType.CODEC, true), (asset, value) -> asset.type = value, asset -> asset.type)
        .add()
        .build();

    @Nonnull
    private MarkerType type = MarkerType.Empty;

    @Override
    public Prop build(@Nonnull Argument var1) {
        if (super.skip()) {
            return EmptyProp.INSTANCE;
        } else {
            return new MarkerProp(this.type);
        }
    }
}
