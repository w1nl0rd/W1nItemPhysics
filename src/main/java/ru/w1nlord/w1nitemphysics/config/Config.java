package ru.w1nlord.w1nitemphysics.config;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

import static net.minecraft.client.render.model.json.ModelVariantMap.GSON;

public final class Config {
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("w1nitemphysics.json");
    private static Data data = new Data();

    private Config() {}

    public static final class Data {
        public Mode mode = Mode.TWO_D;
    }


    public enum Mode {
        NORMAL, THREE_D, TWO_D
    }

    public static Mode getMode() {
        return data.mode;
    }

    public static void setMode(Mode mode) {
        data.mode = mode == null ? Mode.TWO_D : mode;
        save();
    }

    public static void load() {
        try {
            if (Files.exists(PATH)) {
                Data loaded = GSON.fromJson(Files.readString(PATH), Data.class);
                if (loaded != null) data = loaded;
            }
        } catch (Exception e) {
            System.err.println("Failed to load W1nItemPhysics config: " + e.getMessage());
        }

        if (data.mode == null) data.mode = Mode.NORMAL;
    }

    public static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(data));
        } catch (Exception e) {
            System.err.println("Failed to save W1nItemPhysics config: " + e.getMessage());
        }
    }
}