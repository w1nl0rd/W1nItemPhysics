
package ru.w1nlord.w1nitemphysics;

import net.fabricmc.api.ModInitializer;
import ru.w1nlord.w1nitemphysics.config.Config;

public final class W1nItemPhysics implements ModInitializer {
    @Override
    public void onInitialize() {
        Config.load();
    }
}
