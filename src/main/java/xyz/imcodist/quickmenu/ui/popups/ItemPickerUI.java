package xyz.imcodist.quickmenu.ui.popups;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.OverlayContainer;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import xyz.imcodist.quickmenu.data.ActionButtonData;
import xyz.imcodist.quickmenu.ui.components.QuickMenuButton;
import xyz.imcodist.quickmenu.ui.surfaces.SwitcherSurface;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemPickerUI extends OverlayContainer<FlowLayout> {
    public ItemStack selectedItem;
    public String customModelData;

    public Consumer<ItemStack> onSelectedItem;

    public ItemPickerUI() {
        super(Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100)));
        FlowLayout rootComponent = child;

        // Setup root.
        rootComponent
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        // Set up the main layout.
        FlowLayout mainLayout = Containers.verticalFlow(Sizing.fixed(230), Sizing.fixed(210));
        mainLayout
                .surface(new SwitcherSurface())
                .padding(Insets.of(10));
        rootComponent.child(mainLayout);

        // Set up the search bar and its icon.
        FlowLayout searchBoxLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(20));
        searchBoxLayout
                .verticalAlignment(VerticalAlignment.TOP)
                .surface(Surface.flat(0xFFFFFF));
        mainLayout.child(searchBoxLayout);

        TextureComponent textureComponent = Components.texture(
                Identifier.of("quickmenu", "textures/search_icon.png"),
                0, 0,
                12, 12,
                12, 12
        );
        searchBoxLayout.child(textureComponent);

        TextBoxComponent searchBox = Components.textBox(Sizing.fill(100));
        searchBox.setDrawsBackground(false);
        searchBox.margins(Insets.of(0, 0, 4, 0));
        searchBoxLayout.child(searchBox);

        // Setup item buttons.
        FlowLayout itemsLayout = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        ScrollContainer<FlowLayout> itemsScrollContainer = Containers.verticalScroll(Sizing.fill(100), Sizing.fixed(210 - 40), itemsLayout);

        mainLayout.child(itemsScrollContainer);

        createItemButtons(itemsLayout, "");

        // Add function to the search box.
        searchBox.onChanged().subscribe((text) -> createItemButtons(itemsLayout, text));
    }

    public void createItemButtons(FlowLayout parent, String search) {
        // Clear the old buttons.
        parent.clearChildren();

        // Create a list of items and add only those that fit the search.
        List<Item> items = new ArrayList<>();
        Registries.ITEM.forEach((item) -> {
            String itemName = item.getName().getString().toLowerCase();

            if (search.isEmpty() || itemName.contains(search.toLowerCase())) {
                items.add(item);
            }
        });

        if (items.isEmpty()) return;

        // Create each button in different rows.
        int curItem = 0;
        double rowSize = 8;
        for (int i = 0; i < Math.ceil((double) items.size() / rowSize); i++) {
            FlowLayout rowLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());

            for (int x = 0; x < rowSize; x++) {
                if (curItem >= items.size()) break;

                ItemStack item = items.get(curItem).getDefaultStack();

                if (customModelData != null) {
                    ActionButtonData.CustomModelDataValues values = new ActionButtonData.CustomModelDataValues(customModelData);
                    item.set(DataComponentTypes.CUSTOM_MODEL_DATA, values.getComponent());
                }

                ButtonComponent button = new QuickMenuButton(item, (buttonComponent) -> {
                    // On Left Click.
                    selectedItem = item;
                    remove();
                }, (quickMenuButton) -> {});
                button.tooltip(item.getName());

                rowLayout.child(button);
                curItem++;
            }

            parent.child(rowLayout);
        }
    }

    @Override
    public void remove() {
        super.remove();
        onSelectedItem.accept(selectedItem);
    }
}
