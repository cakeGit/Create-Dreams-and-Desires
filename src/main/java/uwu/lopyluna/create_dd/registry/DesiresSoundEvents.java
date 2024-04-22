package uwu.lopyluna.create_dd.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import uwu.lopyluna.create_dd.DesiresCreate;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

//@EventBusSubscriber(bus = Bus.FORGE)
public class DesiresSoundEvents {

	public static final Map<ResourceLocation, SoundEntry> ALL = new HashMap<>();

//	public static final SoundEntry

//		MECHANICAL_PRESS_ACTIVATION = create("mechanical_press_activation").subtitle("Mechanical Press clangs")
//			.playExisting(SoundEvents.ANVIL_LAND, .125f, 1f)
//			.playExisting(SoundEvents.ITEM_BREAK, .5f, 1f)
//			.category(SoundSource.BLOCKS)
//			.build(),

//		MECHANICAL_PRESS_ACTIVATION_ON_BELT =
//			create("mechanical_press_activation_belt").subtitle("Mechanical Press bonks")
//				.playExisting(SoundEvents.WOOL_HIT, .75f, 1f)
//				.playExisting(SoundEvents.ITEM_BREAK, .15f, .75f)
//				.category(SoundSource.BLOCKS)
//				.build(),

//		CRANKING = create("cranking").subtitle("Hand Crank turns")
//			.playExisting(SoundEvents.WOOD_PLACE, .075f, .5f)
//			.playExisting(SoundEvents.WOODEN_BUTTON_CLICK_OFF, .025f, .5f)
//			.category(SoundSource.BLOCKS)
//			.build(),

//		WORLDSHAPER_PLACE = create("worldshaper_place").subtitle("Worldshaper zaps")
//			.playExisting(SoundEvents.NOTE_BLOCK_BASEDRUM)
//			.category(SoundSource.PLAYERS)
//			.build(),

//		SAW_ACTIVATE_WOOD = create("saw_activate_wood").subtitle("Mechanical Saw activates")
//			.playExisting(SoundEvents.BOAT_PADDLE_LAND, .75f, 1.5f)
//			.category(SoundSource.BLOCKS)
//			.build(),

//		SAW_ACTIVATE_STONE = create("saw_activate_stone").subtitle("Mechanical Saw activates")
//			.playExisting(SoundEvents.UI_STONECUTTER_TAKE_RESULT, .125f, 1.25f)
//			.category(SoundSource.BLOCKS)
//			.build(),

//		BRONZE_BELL_USE = create("bronze_bell_use").subtitle("Bronze Bell rung")
//			.playExisting(SoundEvents.BELL_BLOCK)
//			.category(SoundSource.BLOCKS)
//			.build(),

//		BELLOW = create("bellow").subtitle("Blew noises")
//			.playExisting(AllSoundEvents.STEAM.getMainEvent())
//			.category(SoundSource.NEUTRAL)
//			.attenuationDistance(32)
//			.build();

	private static SoundEntryBuilder create(String name) {
		return create(DesiresCreate.asResource(name));
	}

	public static SoundEntryBuilder create(ResourceLocation id) {
		return new SoundEntryBuilder(id);
	}

	public static void prepare() {
		for (SoundEntry entry : ALL.values())
			entry.prepare();
	}

	public static void register(RegisterEvent event) {
		event.register(Registry.SOUND_EVENT_REGISTRY, helper -> {
			for (SoundEntry entry : ALL.values())
				entry.register(helper);
		});
	}

	public static void provideLang(BiConsumer<String, String> consumer) {
		for (SoundEntry entry : ALL.values())
			if (entry.hasSubtitle())
				consumer.accept(entry.getSubtitleKey(), entry.getSubtitle());
	}

	public static SoundEntryProvider provider(DataGenerator generator) {
		return new SoundEntryProvider(generator);
	}

	public static void playItemPickup(Player player) {
		player.level.playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f,
			1f + DesiresCreate.RANDOM.nextFloat());
	}

//	@SubscribeEvent
//	public static void cancelSubtitlesOfCompoundedSounds(PlaySoundEvent event) {
//		ResourceLocation soundLocation = event.getSound().getSoundLocation();
//		if (!soundLocation.getNamespace().equals(Create.ID))
//			return;
//		if (soundLocation.getPath().contains("_compounded_")
//			event.setResultSound();
//
//	}

	private static class SoundEntryProvider implements DataProvider {

		private DataGenerator generator;

		public SoundEntryProvider(DataGenerator generator) {
			this.generator = generator;
		}

		@Override
		public void run(CachedOutput cache) throws IOException {
			generate(generator.getOutputFolder(), cache);
		}

		@Override
		public String getName() {
			return "Create's Custom Sounds";
		}

		public void generate(Path path, CachedOutput cache) {
			path = path.resolve("assets/create");

			try {
				JsonObject json = new JsonObject();
				ALL.entrySet()
					.stream()
					.sorted(Map.Entry.comparingByKey())
					.forEach(entry -> {
						entry.getValue()
							.write(json);
					});
				DataProvider.saveStable(cache, json, path.resolve("sounds.json"));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public record ConfiguredSoundEvent(Supplier<SoundEvent> event, float volume, float pitch) {
	}

	public static class SoundEntryBuilder {

		protected ResourceLocation id;
		protected String subtitle = "unregistered";
		protected SoundSource category = SoundSource.BLOCKS;
		protected List<ConfiguredSoundEvent> wrappedEvents;
		protected List<ResourceLocation> variants;
		protected int attenuationDistance;

		public SoundEntryBuilder(ResourceLocation id) {
			wrappedEvents = new ArrayList<>();
			variants = new ArrayList<>();
			this.id = id;
		}

		public SoundEntryBuilder subtitle(String subtitle) {
			this.subtitle = subtitle;
			return this;
		}

		public SoundEntryBuilder attenuationDistance(int distance) {
			this.attenuationDistance = distance;
			return this;
		}

		public SoundEntryBuilder noSubtitle() {
			this.subtitle = null;
			return this;
		}

		public SoundEntryBuilder category(SoundSource category) {
			this.category = category;
			return this;
		}

		public SoundEntryBuilder addVariant(String name) {
			return addVariant(DesiresCreate.asResource(name));
		}

		public SoundEntryBuilder addVariant(ResourceLocation id) {
			variants.add(id);
			return this;
		}

		public SoundEntryBuilder playExisting(Supplier<SoundEvent> event, float volume, float pitch) {
			wrappedEvents.add(new ConfiguredSoundEvent(event, volume, pitch));
			return this;
		}

		public SoundEntryBuilder playExisting(SoundEvent event, float volume, float pitch) {
			return playExisting(() -> event, volume, pitch);
		}

		public SoundEntryBuilder playExisting(SoundEvent event) {
			return playExisting(event, 1, 1);
		}

		public SoundEntry build() {
			SoundEntry entry =
				wrappedEvents.isEmpty() ? new CustomSoundEntry(id, variants, subtitle, category, attenuationDistance)
					: new WrappedSoundEntry(id, subtitle, wrappedEvents, category, attenuationDistance);
			ALL.put(entry.getId(), entry);
			return entry;
		}

	}

	public static abstract class SoundEntry {

		protected ResourceLocation id;
		protected String subtitle;
		protected SoundSource category;
		protected int attenuationDistance;

		public SoundEntry(ResourceLocation id, String subtitle, SoundSource category, int attenuationDistance) {
			this.id = id;
			this.subtitle = subtitle;
			this.category = category;
			this.attenuationDistance = attenuationDistance;
		}

		public abstract void prepare();

		public abstract void register(RegisterEvent.RegisterHelper<SoundEvent> registry);

		public abstract void write(JsonObject json);

		public abstract SoundEvent getMainEvent();

		public String getSubtitleKey() {
			return id.getNamespace() + ".subtitle." + id.getPath();
		}

		public ResourceLocation getId() {
			return id;
		}

		public boolean hasSubtitle() {
			return subtitle != null;
		}

		public String getSubtitle() {
			return subtitle;
		}

		public void playOnServer(Level world, Vec3i pos) {
			playOnServer(world, pos, 1, 1);
		}

		public void playOnServer(Level world, Vec3i pos, float volume, float pitch) {
			play(world, null, pos, volume, pitch);
		}

		public void play(Level world, Player entity, Vec3i pos) {
			play(world, entity, pos, 1, 1);
		}

		public void playFrom(Entity entity) {
			playFrom(entity, 1, 1);
		}

		public void playFrom(Entity entity, float volume, float pitch) {
			if (!entity.isSilent())
				play(entity.level, null, entity.blockPosition(), volume, pitch);
		}

		public void play(Level world, Player entity, Vec3i pos, float volume, float pitch) {
			play(world, entity, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, volume, pitch);
		}

		public void play(Level world, Player entity, Vec3 pos, float volume, float pitch) {
			play(world, entity, pos.x(), pos.y(), pos.z(), volume, pitch);
		}

		public abstract void play(Level world, Player entity, double x, double y, double z, float volume, float pitch);

		public void playAt(Level world, Vec3i pos, float volume, float pitch, boolean fade) {
			playAt(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, volume, pitch, fade);
		}

		public void playAt(Level world, Vec3 pos, float volume, float pitch, boolean fade) {
			playAt(world, pos.x(), pos.y(), pos.z(), volume, pitch, fade);
		}

		public abstract void playAt(Level world, double x, double y, double z, float volume, float pitch, boolean fade);

	}

	private static class WrappedSoundEntry extends SoundEntry {

		private List<ConfiguredSoundEvent> wrappedEvents;
		private List<CompiledSoundEvent> compiledEvents;

		public WrappedSoundEntry(ResourceLocation id, String subtitle,
			List<ConfiguredSoundEvent> wrappedEvents, SoundSource category, int attenuationDistance) {
			super(id, subtitle, category, attenuationDistance);
			this.wrappedEvents = wrappedEvents;
			compiledEvents = new ArrayList<>();
		}

		@Override
		public void prepare() {
			for (int i = 0; i < wrappedEvents.size(); i++) {
				ConfiguredSoundEvent wrapped = wrappedEvents.get(i);
				ResourceLocation location = getIdOf(i);
				RegistryObject<SoundEvent> event = RegistryObject.create(location, ForgeRegistries.SOUND_EVENTS);
				compiledEvents.add(new CompiledSoundEvent(event, wrapped.volume(), wrapped.pitch()));
			}
		}

		@Override
		public void register(RegisterEvent.RegisterHelper<SoundEvent> helper) {
			for (CompiledSoundEvent compiledEvent : compiledEvents) {
				ResourceLocation location = compiledEvent.event().getId();
				helper.register(location, new SoundEvent(location));
			}
		}

		@Override
		public SoundEvent getMainEvent() {
			return compiledEvents.get(0)
				.event().get();
		}

		protected ResourceLocation getIdOf(int i) {
			return new ResourceLocation(id.getNamespace(), i == 0 ? id.getPath() : id.getPath() + "_compounded_" + i);
		}

		@Override
		public void write(JsonObject json) {
			for (int i = 0; i < wrappedEvents.size(); i++) {
				ConfiguredSoundEvent event = wrappedEvents.get(i);
				JsonObject entry = new JsonObject();
				JsonArray list = new JsonArray();
				JsonObject s = new JsonObject();
				s.addProperty("name", event.event()
					.get()
					.getLocation()
					.toString());
				s.addProperty("type", "event");
				if (attenuationDistance != 0)
					s.addProperty("attenuation_distance", attenuationDistance);
				list.add(s);
				entry.add("sounds", list);
				if (i == 0 && hasSubtitle())
					entry.addProperty("subtitle", getSubtitleKey());
				json.add(getIdOf(i).getPath(), entry);
			}
		}

		@Override
		public void play(Level world, Player entity, double x, double y, double z, float volume, float pitch) {
			for (CompiledSoundEvent event : compiledEvents) {
				world.playSound(entity, x, y, z, event.event().get(), category, event.volume() * volume,
					event.pitch() * pitch);
			}
		}

		@Override
		public void playAt(Level world, double x, double y, double z, float volume, float pitch, boolean fade) {
			for (CompiledSoundEvent event : compiledEvents) {
				world.playLocalSound(x, y, z, event.event().get(), category, event.volume() * volume,
					event.pitch() * pitch, fade);
			}
		}

		private record CompiledSoundEvent(RegistryObject<SoundEvent> event, float volume, float pitch) {
		}

	}

	private static class CustomSoundEntry extends SoundEntry {

		protected List<ResourceLocation> variants;
		protected RegistryObject<SoundEvent> event;

		public CustomSoundEntry(ResourceLocation id, List<ResourceLocation> variants, String subtitle,
			SoundSource category, int attenuationDistance) {
			super(id, subtitle, category, attenuationDistance);
			this.variants = variants;
		}

		@Override
		public void prepare() {
			event = RegistryObject.create(id, ForgeRegistries.SOUND_EVENTS);
		}

		@Override
		public void register(RegisterEvent.RegisterHelper<SoundEvent> helper) {
			ResourceLocation location = event.getId();
			helper.register(location, new SoundEvent(location));
		}

		@Override
		public SoundEvent getMainEvent() {
			return event.get();
		}

		@Override
		public void write(JsonObject json) {
			JsonObject entry = new JsonObject();
			JsonArray list = new JsonArray();

			JsonObject s = new JsonObject();
			s.addProperty("name", id.toString());
			s.addProperty("type", "file");
			if (attenuationDistance != 0)
				s.addProperty("attenuation_distance", attenuationDistance);
			list.add(s);

			for (ResourceLocation variant : variants) {
				s = new JsonObject();
				s.addProperty("name", variant.toString());
				s.addProperty("type", "file");
				if (attenuationDistance != 0)
					s.addProperty("attenuation_distance", attenuationDistance);
				list.add(s);
			}

			entry.add("sounds", list);
			if (hasSubtitle())
				entry.addProperty("subtitle", getSubtitleKey());
			json.add(id.getPath(), entry);
		}

		@Override
		public void play(Level world, Player entity, double x, double y, double z, float volume, float pitch) {
			world.playSound(entity, x, y, z, event.get(), category, volume, pitch);
		}

		@Override
		public void playAt(Level world, double x, double y, double z, float volume, float pitch, boolean fade) {
			world.playLocalSound(x, y, z, event.get(), category, volume, pitch, fade);
		}

	}

}