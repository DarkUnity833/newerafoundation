package net.darkunity.newerafoundation.network;

import net.darkunity.newerafoundation.inventory.StorageMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Client -> server: current text of the terminal's search field. */
public record StorageSearchPayload(int containerId, String text) implements CustomPacketPayload {
    public static final Type<StorageSearchPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("newerafoundation", "storage_search"));
    public static final StreamCodec<FriendlyByteBuf, StorageSearchPayload> CODEC = StreamCodec.<FriendlyByteBuf, StorageSearchPayload, Integer, String>composite(
            ByteBufCodecs.VAR_INT, StorageSearchPayload::containerId,
            ByteBufCodecs.STRING_UTF8, StorageSearchPayload::text,
            StorageSearchPayload::new);

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(StorageSearchPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player().level().isClientSide) return; // server-bound only
            if (ctx.player().containerMenu instanceof StorageMenu menu && menu.containerId == payload.containerId()) menu.setSearch(payload.text());
        });
    }
}
