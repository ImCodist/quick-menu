package xyz.imcodist;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.util.InputUtil;
import xyz.imcodist.other.ActionButtonDataHandler;
import xyz.imcodist.other.ModConfig;
import xyz.imcodist.other.ModKeybindings;
import xyz.imcodist.ui.MainUI;

public class QuickMenu implements ModInitializer {
    public static final ModConfig CONFIG = ModConfig.createAndLoad();

    @Override
    public void onInitialize() {
        // Initialize the mods keybinds and data handler.
        ModKeybindings.initialize();
        ActionButtonDataHandler.initialize();

        // On the end of each tick check to see if the keybind has been pressed.
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            while (ModKeybindings.menuOpenKeybinding.wasPressed()) {
                client.setScreen(new MainUI());
            }

            if (client.currentScreen == null) {
                ActionButtonDataHandler.actions.forEach((actionButtonData) -> {
                    InputUtil.Key key = actionButtonData.getKey();
                    if (key == null) return;

                    long handle = client.getWindow().getHandle();
                    if (InputUtil.isKeyPressed(handle, key.getCode())) {
                        if (!actionButtonData.keyPressed) {
                            actionButtonData.run();
                        }

                        actionButtonData.keyPressed = true;
                    } else {
                        actionButtonData.keyPressed = false;
                    }
                });
            }
        });
    }
}