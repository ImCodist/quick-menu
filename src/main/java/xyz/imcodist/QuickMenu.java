package xyz.imcodist;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import xyz.imcodist.other.Keybindings;
import xyz.imcodist.ui.MainGameUI;

public class QuickMenu implements ModInitializer {
    @Override
    public void onInitialize() {
        Keybindings.initialize();

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            while (Keybindings.menuOpenKeybinding.wasPressed()) {
                client.setScreen(new MainGameUI());
            }
        });
    }
}