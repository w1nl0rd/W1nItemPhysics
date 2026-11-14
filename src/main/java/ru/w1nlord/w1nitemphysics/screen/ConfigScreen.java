package ru.w1nlord.w1nitemphysics.screen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import ru.w1nlord.w1nitemphysics.config.Config;

public final class ConfigScreen implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SettingsScreen::new;
    }

    private static final class SettingsScreen extends Screen {
        private final Screen parent;
        private ButtonWidget modeButton;

        private SettingsScreen(Screen parent) {
            super(Text.literal("W1nItemPhysics"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            int centerX = width / 2;
            int startY = height / 2 - 20;

            modeButton = ButtonWidget.builder(getModeText(), button -> {
                Config.Mode next = switch (Config.getMode()) {
                    case NORMAL -> Config.Mode.THREE_D;
                    case THREE_D -> Config.Mode.TWO_D;
                    case TWO_D -> Config.Mode.NORMAL;
                };

                Config.setMode(next);
                button.setMessage(getModeText());
            }).dimensions(centerX - 100, startY, 200, 20).build();

            addDrawableChild(modeButton);
            addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), button -> close()).dimensions(centerX - 100, startY + 25, 200, 20).build());
        }

        private Text getModeText() {
            String mode = switch (Config.getMode()) {
                case NORMAL -> "Обычный";
                case THREE_D -> "3D";
                case TWO_D -> "2D";
            };

            return Text.literal("Режим: " + mode);
        }

        @Override
        public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);
            context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 50, 0xFFFFFF);
        }

        @Override
        public void close() {
            client.setScreen(parent);
        }
    }
}