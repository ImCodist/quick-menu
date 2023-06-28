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
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import xyz.imcodist.data.ActionButtonData;
import xyz.imcodist.data.command_actions.BaseActionData;
import xyz.imcodist.data.command_actions.CommandActionData;
import xyz.imcodist.other.ActionButtonDataHandler;
import xyz.imcodist.ui.components.QuickMenuButton;
import xyz.imcodist.ui.surfaces.SwitcherSurface;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ActionEditorUI extends BaseOwoScreen<FlowLayout> {
    ActionButtonData actionData = new ActionButtonData();
    boolean newAction = true;

    FlowLayout actionsLayout;
    ArrayList<Component> actionsSource = new ArrayList<>();
    ArrayList<BaseActionData> actionArray = new ArrayList<>();

    private ItemPickerUI itemPicker;

    public BaseOwoScreen<FlowLayout> previousScreen;

    public ActionEditorUI(ActionButtonData action) {
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

        // Set up the main layout.
        int mainLayoutHeight = 206;
        FlowLayout mainLayout = Containers.verticalFlow(Sizing.fixed(210), Sizing.fixed(mainLayoutHeight));
        mainLayout.surface(new SwitcherSurface());
        rootComponent.child(mainLayout);

        // Set up the header.
        int headerLayoutHeight = 6*4;
        FlowLayout headerLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(headerLayoutHeight));
        headerLayout
                .surface(new SwitcherSurface(true))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        mainLayout.child(headerLayout);

        LabelComponent headerLabel = Components.label(Text.translatable("menu.editor.title"));
        headerLayout.child(headerLabel);

        // Set up the property containers
        FlowLayout propertiesLayout = Containers.verticalFlow(Sizing.fill(100), Sizing.content());

        int propertiesScrollHeight = mainLayoutHeight - headerLayoutHeight;
        ScrollContainer<Component> propertiesScroll = Containers.verticalScroll(Sizing.fill(100), Sizing.fixed(propertiesScrollHeight), propertiesLayout);
        propertiesScroll.padding(Insets.of(5, 5, 8, 8));
        mainLayout.child(propertiesScroll);

        // Name property
        FlowLayout nameProperty = createNewProperty("name");
        nameProperty.padding(nameProperty.padding().get().withTop(0));
        TextBoxComponent nameTextBox = Components.textBox(Sizing.fixed(100), actionData.name);
        nameTextBox.cursorStyle(CursorStyle.TEXT);

        nameProperty.child(nameTextBox);
        propertiesLayout.child(nameProperty);

        // Icon property
        FlowLayout iconProperty = createNewProperty("icon");
        QuickMenuButton iconButton = new QuickMenuButton(actionData.icon, (buttonComponent) -> {
            QuickMenuButton quickMenuButton = (QuickMenuButton) buttonComponent;

            itemPicker = new ItemPickerUI();

            itemPicker.selectedItem = quickMenuButton.itemIcon;
            quickMenuButton.itemIcon = null;

            itemPicker.onSelectedItem = (item) -> quickMenuButton.itemIcon = item;

            rootComponent.child(itemPicker);
        }, (quickMenuButton) -> {});

        iconProperty.child(iconButton);
        propertiesLayout.child(iconProperty);

        // Actions
        actionArray = new ArrayList<>(actionData.actions);
        createActions(propertiesLayout);

        // Set up the editor buttons.
        FlowLayout buttonsLayout = Containers.horizontalFlow(Sizing.content(), Sizing.content());
        buttonsLayout
                .surface(Surface.DARK_PANEL)
                .padding(Insets.of(6))
                .margins(Insets.of(10, 0, 0, 0));

        rootComponent.child(buttonsLayout);

        ButtonComponent finishButton = Components.button(Text.translatable("menu.editor.button.finish"), (buttonComponent) -> {
            actionData.name = nameTextBox.getText();
            if (iconButton.itemIcon != null) actionData.icon = iconButton.itemIcon;

            //actionData.action = actionTextBox.getText();
            actionData.actions = actionArray;
            updateActionData();

            if (newAction) {
                ActionButtonDataHandler.add(actionData);
            } else {
                ActionButtonDataHandler.save();
            }

            close();
        });
        ButtonComponent cancelButton = Components.button(
                Text.translatable("menu.editor.button.cancel"),
                (buttonComponent) -> close()
        );

        buttonsLayout.child(finishButton);
        buttonsLayout.gap(4);
        buttonsLayout.child(cancelButton);
    }

    public FlowLayout createNewProperty(String name) {
        return createNewProperty(name, true, true);
    }

    public FlowLayout createNewProperty(String name, boolean underline) {
        return createNewProperty(name, underline, true);
    }

    public FlowLayout createNewProperty(String name, boolean underline, boolean useTranslatable) {
        FlowLayout layout = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
        layout
                .padding(Insets.of(4, 0, 0, 5))
                .alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER);

        MutableText labelText;
        if (useTranslatable) labelText = Text.translatable("menu.editor.property." + name);
        else labelText = Text.literal(name);

        if (underline) labelText.formatted(Formatting.UNDERLINE);

        LabelComponent label = Components.label(labelText);
        label.positioning(Positioning.relative(0, 50));

        layout.child(label);

        return layout;
    }

    public void createActions(FlowLayout layout) {
        updateActionData();

        actionsSource.clear();
        if (actionsLayout != null) actionsLayout.remove();

        actionsLayout = createActionsLayout();
        layout.child(actionsLayout);

        AtomicInteger i = new AtomicInteger();
        actionArray.forEach((action) -> {
            String name = "ACT";
            if (action instanceof CommandActionData) name = "CMD";

            FlowLayout property = createNewProperty(name + " #" + (i.get() + 1), false, false);
            Component source = null;

            if (action instanceof CommandActionData commandAction) {
                TextBoxComponent textBoxComponent = Components.textBox(Sizing.fill(57));

                textBoxComponent.setMaxLength(200);
                textBoxComponent.text(commandAction.command);

                property.child(textBoxComponent);
                source = textBoxComponent;
            }

            ButtonComponent removeActionButton = Components.button(Text.literal(" - "), (buttonComponent -> {
                int currentIndex = actionArray.indexOf(action);
                actionsSource.remove(currentIndex);
                actionArray.remove(action);

                assert actionsLayout.parent() != null;
                createActions((FlowLayout) actionsLayout.parent());
            }));
            removeActionButton.margins(Insets.of(0, 0, 4, 0));
            property.child(removeActionButton);

            actionsLayout.child(property);

            actionsSource.add(source);
            i.addAndGet(1);
        });

        // New Action Button
        FlowLayout actionLayout = createNewProperty("new_action", false);
        actionLayout.padding(actionLayout.padding().get().add(0, 6, 0, 0));

        ButtonComponent newActionButton = Components.button(Text.literal(" + "), (buttonComponent -> {
            actionArray.add(new CommandActionData());
            createActions((FlowLayout) actionsLayout.parent());
        }));

        actionLayout.child(newActionButton);
        actionsLayout.child(actionLayout);
    }

    public FlowLayout createActionsLayout() {
        FlowLayout layout = Containers.collapsible(Sizing.fill(100), Sizing.content(), Text.translatable("menu.editor.actions"), true);
        layout
                .padding(Insets.of(4, 0, -5, 5))
                .verticalAlignment(VerticalAlignment.CENTER);

        return layout;
    }

    public void updateActionData() {
        if (actionsSource.isEmpty()) return;

        AtomicInteger i = new AtomicInteger();
        actionArray.forEach((action) -> {
            if (actionsSource.size() <= i.get()) return;
            Component source = actionsSource.get(i.get());

            if (action instanceof CommandActionData commandAction) {
                TextBoxComponent textBoxSource = (TextBoxComponent) source;
                commandAction.command = textBoxSource.getText();
            }

            i.addAndGet(1);
        });
    }

    @Override
    public void close() {
        if (itemPicker != null && itemPicker.parent() != null) {
            itemPicker.remove();

            itemPicker = null;
            return;
        }

        if (previousScreen != null) MinecraftClient.getInstance().setScreen(previousScreen);
        else super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
