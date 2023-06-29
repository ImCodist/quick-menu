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

        // On the end of each tick check to see if a keybind has been pressed.
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            // Check for menu open keybind.
            while (ModKeybindings.menuOpenKeybinding.wasPressed()) {
                client.setScreen(new MainUI());
            }

            // Check for action buttons keybinds.
            // I really dont like this.
            if (client.currentScreen == null) {
                ActionButtonDataHandler.actions.forEach((actionButtonData) -> {
                    if (actionButtonData.keybind.get(3) == 0) {
                        // Key press.
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
                    } else {
                        // Mouse press.
                        // TODO: Allow buttons greater then 2 to be bound.
                        int mouseButton = actionButtonData.keybind.get(0);
                        boolean pressed = false;

                        switch (mouseButton) {
                            case 0 -> pressed = client.mouse.wasLeftButtonClicked();
                            case 1 -> pressed = client.mouse.wasRightButtonClicked();
                            case 2 -> pressed = client.mouse.wasMiddleButtonClicked();
                        }

                        if (pressed) {
                            actionButtonData.run();
                        }
                    }
                });
            }
        });
    }
}