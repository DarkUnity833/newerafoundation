package net.darkunity.newerafoundation;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.fml.util.thread.SidedThreadGroups;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.IEventBus;

import net.minecraft.server.TickTask;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;

import net.darkunity.newerafoundation.init.*;
import net.darkunity.newerafoundation.client.gui.TieredShulkerScreen;
import net.darkunity.newerafoundation.client.gui.CustomChestScreen;
import net.darkunity.newerafoundation.client.gui.CustomBarrelScreen;
import net.darkunity.newerafoundation.client.gui.BackpackScreen;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

import it.unimi.dsi.fastutil.ints.IntObjectPair;
import it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;

@Mod("newerafoundation")
public class NewerafoundationMod {
	public static final Logger LOGGER = LogManager.getLogger(NewerafoundationMod.class);
	public static final String MODID = "newerafoundation";

	public NewerafoundationMod(IEventBus modEventBus) {
		// Start of user code block mod constructor
		// 1. Регистрация блоков и предметов
		ModBlocks.BLOCKS.register(modEventBus);
		ModBlocks.ITEMS.register(modEventBus);
		ModItems.ITEMS.register(modEventBus);
		// --- РЕГИСТРАЦИЯ РЕЦЕПТОВ ---
		ModRecipes.SERIALIZERS.register(modEventBus);
		// 2. Регистрация сущностей (Block Entities)
		ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
		// 3. Регистрация типов МЕНЮ
		ModMenuTypes.MENUS.register(modEventBus);
		// 4. ВКЛАДКА КРЕАТИВА
		ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
		// 5. Сетевые пакеты
		modEventBus.addListener((RegisterPayloadHandlersEvent event) -> {
			// Регистрация пакетов здесь
		});
		// 6. РЕГИСТРАЦИЯ ЭКРАНОВ
		modEventBus.addListener((RegisterMenuScreensEvent event) -> {
			event.register(ModMenuTypes.BARREL_MENU.get(), CustomBarrelScreen::new);
			event.register(ModMenuTypes.CHEST_MENU.get(), CustomChestScreen::new);
			event.register(ModMenuTypes.TIERED_SHULKER_MENU.get(), TieredShulkerScreen::new);
			event.register(ModMenuTypes.BACKPACK_MENU.get(), BackpackScreen::new);
			event.register(ModMenuTypes.VOID_BARREL_MENU.get(), net.darkunity.newerafoundation.client.gui.VoidBarrelScreen::new);
			event.register(ModMenuTypes.STORAGE_MENU.get(), net.darkunity.newerafoundation.client.gui.StorageScreen::new);
		});
		// 6.1 РЕГИСТРАЦИЯ РЕНДЕРЕРОВ
		modEventBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
			// Сундуки (пакет render)
			event.registerBlockEntityRenderer(ModBlockEntities.CHEST_TYPE.get(), net.darkunity.newerafoundation.client.render.CustomChestRenderer::new);
			// Баки (пакет renderer)
			event.registerBlockEntityRenderer(ModBlockEntities.TANK_TYPE.get(), net.darkunity.newerafoundation.client.renderer.CustomTankRenderer::new);
			// Шалкербоксы (анимированная крышка)
			event.registerBlockEntityRenderer(ModBlockEntities.TIERED_SHULKER_TYPE.get(), net.darkunity.newerafoundation.client.render.TieredShulkerRenderer::new);
		});
		modEventBus.addListener((EntityRenderersEvent.RegisterLayerDefinitions event) -> {
			event.registerLayerDefinition(net.darkunity.newerafoundation.client.render.TieredShulkerRenderer.LAYER, net.darkunity.newerafoundation.client.render.TieredShulkerRenderer::createLayer);
		});
		// 6.2 РЕГИСТРАЦИЯ CAPABILITIES (Для взаимодействия с жидкостями)
		modEventBus.addListener((RegisterCapabilitiesEvent event) -> {
			event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.TANK_TYPE.get(), (be, side) -> {
				if (be instanceof net.darkunity.newerafoundation.block.entity.CustomTankBlockEntity tank) {
					return tank.fluidTank;
				}
				return null;
			});
			// --- Новые блоки: capabilities ---
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.CHEST_TYPE.get(), (be, side) -> new net.neoforged.neoforge.items.wrapper.InvWrapper(be));
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.TIERED_SHULKER_TYPE.get(), (be, side) -> new net.neoforged.neoforge.items.wrapper.InvWrapper(be));
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BARREL_TYPE.get(), (be, side) -> new net.neoforged.neoforge.items.wrapper.InvWrapper(be));
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.VOID_BARREL_TYPE.get(), (be, side) -> be.handler);
			event.registerBlock(Capabilities.FluidHandler.BLOCK, (lvl, pos, st, be, side) -> net.darkunity.newerafoundation.block.entity.VoidHandlers.FLUIDS, ModBlocks.VOID_TANK.get());
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.VACUUM_CHEST_TYPE.get(), (be, side) -> new net.neoforged.neoforge.items.wrapper.InvWrapper(be));
			event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.PIPE_TYPE.get(), (be, side) -> be.tank);
			event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.LAVA_FEEDER_TYPE.get(), (be, side) -> be.inputOnly);
		});
		// 6.3 Пакет поиска терминала хранилища
		addNetworkMessage(net.darkunity.newerafoundation.network.StorageSearchPayload.TYPE, net.darkunity.newerafoundation.network.StorageSearchPayload.CODEC, net.darkunity.newerafoundation.network.StorageSearchPayload::handle);
		// 7. Шина событий NeoForge
		NeoForge.EVENT_BUS.register(this);
		// End of user code block mod constructor
		NeoForge.EVENT_BUS.register(this);
		modEventBus.addListener(this::registerNetworking);
		// Start of user code block mod init
		// End of user code block mod init
	}

	// Start of user code block mod methods
	// End of user code block mod methods
	private static boolean networkingRegistered = false;
	private static final Map<CustomPacketPayload.Type<?>, NetworkMessage<?>> MESSAGES = new HashMap<>();

	private record NetworkMessage<T extends CustomPacketPayload>(StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
	}

	public static <T extends CustomPacketPayload> void addNetworkMessage(CustomPacketPayload.Type<T> id, StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
		if (networkingRegistered)
			throw new IllegalStateException("Cannot register new network messages after networking has been registered");
		MESSAGES.put(id, new NetworkMessage<>(reader, handler));
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void registerNetworking(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(MODID);
		MESSAGES.forEach((id, networkMessage) -> registrar.playBidirectional(id, ((NetworkMessage) networkMessage).reader(), ((NetworkMessage) networkMessage).handler()));
		networkingRegistered = true;
	}

	private static final Queue<IntObjectPair<Runnable>> workToBeScheduled = new ConcurrentLinkedQueue<>();
	private static final PriorityQueue<TickTask> workQueue = new PriorityQueue<>(Comparator.comparingInt(TickTask::getTick));

	public static void queueServerWork(int delay, Runnable action) {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
			workToBeScheduled.add(new IntObjectImmutablePair<>(delay, action));
	}

	@SubscribeEvent
	public void tick(ServerTickEvent.Post event) {
		int currentTick = event.getServer().getTickCount();
		IntObjectPair<Runnable> work;
		while ((work = workToBeScheduled.poll()) != null) {
			workQueue.add(new TickTask(currentTick + work.leftInt(), work.right()));
		}
		while (!workQueue.isEmpty() && currentTick >= workQueue.peek().getTick()) {
			workQueue.poll().run();
		}
	}
}