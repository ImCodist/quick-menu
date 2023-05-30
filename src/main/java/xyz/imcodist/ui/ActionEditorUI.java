package xyz.imcodist.ui;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Insets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import xyz.imcodist.data.ActionData;
import xyz.imcodist.other.ActionDataHandler;
import xyz.imcodist.ui.components.QuickMenuButton;
import xyz.imcodist.ui.surfaces.SwitcherSurface;

public class ActionEditorUI extends BaseOwoScreen<FlowLayout> {
    ActionData actionData = new ActionData();
    boolean newAction = true;

    public BaseOwoScreen<FlowLayout> previousScreen;

    public ActionEditorUI(ActionData action) {
        if (action != null) {
            actionData = action;
            newAction = false;
        }
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        // Setup root.
        rootComponent
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        // Setup the main layout.
        int mainLayoutHeight = 206;
        FlowLayout mainLayout = Containers.verticalFlow(Sizing.fixed(210), Sizing.fixed(mainLayoutHeight));
        mainLayout.surface(new SwitcherSurface());
        rootComponent.child(mainLayout);

        // Setup the header.
        int headerLayoutHeight = 6*4;
        FlowLayout headerLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(headerLayoutHeight));
        headerLayout
                .surface(new SwitcherSurface(true))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        mainLayout.child(headerLayout);

        LabelComponent headerLabel = Components.label(Text.translatable("menu.editor.title"));
        headerLayout.child(headerLabel);

        // Setup the property containers
        FlowLayout propertiesLayout = Containers.verticalFlow(Sizing.fill(100), Sizing.content());

        int propertiesScrollHeight = mainLayoutHeight - headerLayoutHeight;
        ScrollContainer<Component> propertiesScroll = Containers.verticalScroll(Sizing.fill(100), Sizing.fixed(propertiesScrollHeight), propertiesLayout);
        propertiesScroll.padding(Insets.of(0, 5, 8, 8));
        mainLayout.child(propertiesScroll);

        // Name property
        FlowLayout nameProperty = createNewProperty("name");
        TextBoxComponent nameTextBox = Components.textBox(Sizing.fixed(100), actionData.name);
        nameTextBox.cursorStyle(CursorStyle.TEXT);

        nameProperty.child(nameTextBox);
        propertiesLayout.child(nameProperty);

        // Icon property
        FlowLayout iconProperty = createNewProperty("icon");
        QuickMenuButton iconButton = new QuickMenuButton(actionData.icon, (buttonComponent) -> {
            QuickMenuButton quickMenuButton = (QuickMenuButton) buttonComponent;

            ItemPickerUI itemPicker = new ItemPickerUI();
            itemPicker.zIndex(1);

            itemPicker.selectedItem = quickMenuButton.itemIcon;
            quickMenuButton.itemIcon = null;

            itemPicker.onSelectedItem = (item) -> {
                quickMenuButton.itemIcon = item;
            };

            rootComponent.child(itemPicker);
        }, (quickMenuButton) -> {});

        iconProperty.child(iconButton);
        propertiesLayout.child(iconProperty);

        // Action property
        FlowLayout actionProperty = createNewProperty("action");
        TextBoxComponent actionTextBox = Components.textBox(Sizing.fixed(100), actionData.action);

        actionProperty.child(actionTextBox);
        propertiesLayout.child(actionProperty);

        // Setup the editor buttons.
        FlowLayout buttonsLayout = Containers.horizontalFlow(Sizing.content(), Sizing.content());
        buttonsLayout
                .surface(Surface.DARK_PANEL)
                .padding(Insets.of(6))
                .margins(Insets.of(10, 0, 0, 0));

        rootComponent.child(buttonsLayout);

        ButtonComponent finishButton = Components.button(Text.translatable("menu.editor.button.finish"), (buttonComponent) -> {
            actionData.name = nameTextBox.getText();
            if (iconButton.itemIcon != null) actionData.icon = iconButton.itemIcon;

            actionData.action = actionTextBox.getText();

            if (newAction) {
                ActionDataHandler.add(actionData);
            }

            close();
        });
        ButtonComponent cancelButton = Components.button(Text.translatable("menu.editor.button.cancel"), (buttonComponent) -> {
            close();
        });

        buttonsLayout.child(finishButton);
        buttonsLayout.gap(4);
        buttonsLayout.child(cancelButton);
    }

    public FlowLayout createNewProperty(String name) {
        FlowLayout layout = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
        layout
                .padding(Insets.of(4, 0, 0, 0))
                .alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER);

        LabelComponent label = Components.label(Text.translatable("menu.editor.property." + name));
        label.positioning(Positioning.relative(0, 50));

        layout.child(label);

        return layout;
    }

    @Override
    public void close() {
        if (previousScreen != null) MinecraftClient.getInstance().setScreen(previousScreen);
        else super.close();
    }
}
