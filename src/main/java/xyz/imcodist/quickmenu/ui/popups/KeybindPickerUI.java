package xyz.imcodist.quickmenu.ui.popups;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.OverlayContainer;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import xyz.imcodist.quickmenu.other.KeybindHandler;
import xyz.imcodist.quickmenu.ui.surfaces.SwitcherSurface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class KeybindPickerUI extends OverlayContainer<FlowLayout> {
    public Consumer<KeyBinding> onSelectedKeybind;

    public KeybindPickerUI() {
        super(Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100)));
        FlowLayout rootComponent = child;

        // Set up root.
        rootComponent
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        // Set up the main layout.
        FlowLayout mainLayout = Containers.verticalFlow(Sizing.fixed(320), Sizing.fixed(230));
        mainLayout
                .surface(new SwitcherSurface())
                .padding(Insets.of(10));
        rootComponent.child(mainLayout);

        // Set up the scroll layout.
        FlowLayout keybindsLayout = Containers.verticalFlow(Sizing.fill(98), Sizing.content());
        ScrollContainer<FlowLayout> itemsScrollContainer = Containers.verticalScroll(Sizing.fill(100), Sizing.fill(100), keybindsLayout);

        mainLayout.child(itemsScrollContainer);

        // Create action buttons.
        KeyBinding[] keyBindings = KeybindHandler.getKeybindings();

        if (keyBindings != null) {
            Map<String, ArrayList<KeyBinding>> sortedKeybindings = new HashMap<>();
            for (KeyBinding keyBinding : keyBindings) {
                String category = keyBinding.getCategory();

                if (!sortedKeybindings.containsKey(category)) {
                    sortedKeybindings.put(category, new ArrayList<>());
                }

                sortedKeybindings.get(category).add(keyBinding);
            }

            for (String category : sortedKeybindings.keySet()) {
                LabelComponent categoryLabel = Components.label(Text.translatable(category));
                categoryLabel
                        .horizontalTextAlignment(HorizontalAlignment.CENTER)
                        .horizontalSizing(Sizing.fill(100))
                        .margins(Insets.of(4, 0, 0, 0));

                keybindsLayout.child(categoryLabel);

                for (KeyBinding keyBinding : sortedKeybindings.get(category)) {
                    FlowLayout layout = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
                    layout.padding(Insets.of(1, 1, 0, 0));
                    layout.horizontalAlignment(HorizontalAlignment.RIGHT);

                    LabelComponent keyLabel = Components.label(Text.translatable(keyBinding.getTranslationKey()));
                    keyLabel.horizontalTextAlignment(HorizontalAlignment.LEFT);
                    keyLabel.positioning(Positioning.relative(0, 50));
                    layout.child(keyLabel);

                    ButtonComponent buttonSelect = Components.button(Text.translatable("menu.action_picker.select"), (buttonComponent) -> {
                        onSelectedKeybind.accept(keyBinding);
                        remove();
                    });
                    layout.child(buttonSelect);

                    keybindsLayout.child(layout);
                }
            }
        }
    }
}
