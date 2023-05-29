package xyz.imcodist.other;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeybindings {
    public static KeyBinding menuOpenKeybinding;

    public static void initialize() {
        String mainCategory = "key.category.quickmenu";

        menuOpenKeybinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.quickmenu.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                mainCategory
        ));
    }
}
