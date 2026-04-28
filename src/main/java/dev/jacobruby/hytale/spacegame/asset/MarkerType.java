package dev.jacobruby.hytale.spacegame.asset;

import com.hypixel.hytale.codec.codecs.EnumCodec;

public enum MarkerType {

    Empty,
    Terminal,
    Ore_Small,
    Ore_Medium,
    Ore_Large;

    public static final EnumCodec<MarkerType> CODEC = new EnumCodec<>(MarkerType.class);

}
