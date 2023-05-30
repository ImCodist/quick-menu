package xyz.imcodist.ui;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.OverlayContainer;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.imcodist.ui.components.QuickMenuButton;
import xyz.imcodist.ui.surfaces.SwitcherSurface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.function.Consumer;

public class ItemPickerUI extends OverlayContainer<FlowLayout> {
    ItemStack selectedItem;
    public Consumer<ItemStack> onSelectedItem;

    protected ItemPickerUI() {
        super(Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100)));
        FlowLayout rootComponent = child;

        // Setup root.
        rootComponent
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        // Setup the main layout.
        FlowLayout mainLayout = Containers.verticalFlow(Sizing.fixed(230), Sizing.fixed(210));
        mainLayout
                .surface(new SwitcherSurface())
                .padding(Insets.of(10));
        rootComponent.child(mainLayout);

        // Setup search bar.
        FlowLayout searchBoxLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(20));
        searchBoxLayout
                .verticalAlignment(VerticalAlignment.TOP)
                .surface(Surface.flat(0xFFFFFF));
        mainLayout.child(searchBoxLayout);

        TextureComponent textureComponent = Components.texture(
                new Identifier("quickmenu", "textures/search_icon.png"),
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
        searchBox.onChanged().subscribe((text) -> {
            createItemButtons(itemsLayout, text);
        });
    }

    public void createItemButtons(FlowLayout parent, String search) {
        parent.clearChildren();

        List<Item> items = new ArrayList<>();
        int curItem = 0;
        int rowSize = 8;

        Registries.ITEM.forEach((item) -> {
            String itemName = item.getName().getString().toLowerCase();

            if (search == "" || itemName.contains(search.toLowerCase())) {
                items.add(item);
            }
        });

        if (items.size() == 0) return;
        for (int i = 0; i < Math.ceil((double) items.size() / (double) rowSize); i++) {
            FlowLayout rowLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());

            for (int x = 0; x < rowSize; x++) {
                if (curItem >= items.size()) break;

                ItemStack item = items.get(curItem).getDefaultStack();

                ButtonComponent button = new QuickMenuButton(item, (buttonComponent) -> {
                    selectedItem = item;
                    onSelectedItem.accept(selectedItem);

                    remove();
                }, (quickMenuButton) -> {});
                button.tooltip(item.getName());

                rowLayout.child(button);
                curItem += 1;
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
