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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import xyz.imcodist.data.ActionData;
import xyz.imcodist.data.ActionDataHandler;
import xyz.imcodist.ui.components.QuickMenuButton;
import xyz.imcodist.ui.surfaces.SwitcherSurface;

import java.awt.*;
import java.util.concurrent.Flow;

public class ActionEditorUI extends BaseOwoScreen<FlowLayout> {
    ActionData actionData = new ActionData();
    boolean newAction = true;

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
        FlowLayout mainLayout = Containers.verticalFlow(Sizing.fixed(210), Sizing.fixed(206));
        mainLayout.surface(new SwitcherSurface());
        rootComponent.child(mainLayout);

        // Setup the header.
        FlowLayout headerLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(6*4));
        headerLayout
                .surface(new SwitcherSurface(true))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        mainLayout.child(headerLayout);

        LabelComponent headerLabel = Components.label(Text.literal("Action Editor"));
        headerLayout.child(headerLabel);

        // Setup the property containers
        FlowLayout propertiesLayout = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        propertiesLayout.padding(Insets.of(2, 2, 8, 8));

        ScrollContainer<Component> propertiesScroll = Containers.verticalScroll(Sizing.fill(100), Sizing.fill(74), propertiesLayout);
        mainLayout.child(propertiesScroll);

        // Name property
        FlowLayout nameProperty = createNewProperty("name");
        TextBoxComponent nameTextBox = Components.textBox(Sizing.fixed(100), actionData.name);

        nameProperty.child(nameTextBox);
        propertiesLayout.child(nameProperty);

        // Icon property
        FlowLayout iconProperty = createNewProperty("icon");
        QuickMenuButton iconButton = new QuickMenuButton(actionData.icon, (buttonComponent) -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;

            if (player != null) {
                QuickMenuButton quickMenuButton = (QuickMenuButton) buttonComponent;
                quickMenuButton.itemIcon = player.getMainHandStack();
            }
        }, (quickMenuButton) -> {});

        iconProperty.child(iconButton);
        propertiesLayout.child(iconProperty);

        // Action property
        FlowLayout actionProperty = createNewProperty("action");
        TextBoxComponent actionTextBox = Components.textBox(Sizing.fixed(100), actionData.action);

        actionProperty.child(actionTextBox);
        propertiesLayout.child(actionProperty);

        // Setup the editor buttons.
        FlowLayout buttonsLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
        buttonsLayout
                .horizontalAlignment(HorizontalAlignment.RIGHT)
                .padding(Insets.of(5, 5, 5, 5));
        mainLayout.child(buttonsLayout);

        ButtonComponent finishButton = Components.button(Text.literal("Finish"), (buttonComponent) -> {
            actionData.name = nameTextBox.getText();
            actionData.icon = iconButton.itemIcon;

            actionData.action = actionTextBox.getText();

            if (newAction) {
                ActionDataHandler.add(actionData);
            }

            close();
        });
        buttonsLayout.child(finishButton);
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
}
