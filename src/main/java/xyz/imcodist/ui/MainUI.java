package xyz.imcodist.ui;

import io.wispforest.owo.config.ui.ConfigScreen;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import xyz.imcodist.QuickMenu;
import xyz.imcodist.data.ActionButtonData;
import xyz.imcodist.other.ActionButtonDataHandler;
import xyz.imcodist.other.ModKeybindings;
import xyz.imcodist.ui.components.QuickMenuButton;
import xyz.imcodist.ui.surfaces.SwitcherSurface;

public class MainUI extends BaseOwoScreen<FlowLayout> {
    public boolean editMode = false;

    private FlowLayout editorLayout;
    private ActionButtonData hoveredData;

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

        // Create main layout.
        int mainLayoutHeight = QuickMenu.CONFIG.menuHeight();
        FlowLayout mainLayout = Containers.verticalFlow(
                Sizing.fixed(QuickMenu.CONFIG.menuWidth()),
                Sizing.fixed(mainLayoutHeight)
        );
        mainLayout.surface(new SwitcherSurface());
        rootComponent.child(mainLayout);

        // Create edit layout.
        editorLayout = Containers.horizontalFlow(Sizing.fixed(0), Sizing.fixed(0));
        editorLayout.padding(Insets.of(6));
        rootComponent.child(editorLayout);

        // Create header and its components.
        int headerLayoutHeight = 24;
        FlowLayout headerLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(headerLayoutHeight));
        headerLayout
                .surface(new SwitcherSurface(true))
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);
        mainLayout.child(headerLayout);

        // Header label.
        LabelComponent headerLabel = Components.label(Text.translatable("menu.main.title"));
        headerLabel
                .shadow(true);
        headerLayout.child(headerLabel);

        // Header edit button.
        ButtonComponent headerEditButton = Components.button(Text.literal("✎"), (buttonComponent) -> {
            editMode = !editMode;
            updateEditorLayout();
        });
        headerEditButton
                .textShadow(true)
                .renderer(ButtonComponent.Renderer.flat(0x000000, 0x000000, 0x000000))
                .margins(Insets.of(0, 0, 5, 0))
                .horizontalSizing(Sizing.fixed(10));
        headerLayout.child(headerEditButton);

        // Setup action layouts.
        FlowLayout actionFlowLayout = Containers.verticalFlow(Sizing.fill(100), Sizing.content());

        int actionLayoutHeight = mainLayoutHeight - headerLayoutHeight;
        ScrollContainer<Component> actionLayout = Containers.verticalScroll(Sizing.fill(100), Sizing.fixed(actionLayoutHeight), actionFlowLayout);
        actionLayout.padding(Insets.of(2, 5, 2, 2));
        mainLayout.child(actionLayout);

        // Create action buttons.
        createActionButtons(actionFlowLayout);

        // Editor components.
        ButtonComponent newButton = Components.button(
                Text.translatable("menu.main.button.add_action"),
                (buttonComponent) -> gotoActionEditor(null)
        );
        editorLayout.child(newButton);

        editorLayout.gap(5);

        ButtonComponent configButton = Components.button(
                Text.translatable("menu.main.button.config"),
                (buttonComponent) -> {
                    if (client == null) return;
                    client.setScreen(ConfigScreen.create(QuickMenu.CONFIG, null));
                }
        );
        editorLayout.child(configButton);

        // Hide or show the editor layout depending on editing or not.
        updateEditorLayout();
    }

    private void updateEditorLayout() {
        if (!editMode) {
            // Hide the editor layout.
            editorLayout
                    .surface(Surface.BLANK)
                    .margins(Insets.of(0))
                    .sizing(Sizing.fixed(0));
        }
        else {
            // Show the editor layout.
            editorLayout
                    .surface(Surface.DARK_PANEL)
                    .margins(Insets.of(10, 0, 0, 0))
                    .sizing(Sizing.content());
        }
    }

    private void gotoActionEditor(ActionButtonData action) {
        // Create the action editor and set the current screen to it.
        ActionEditorUI actionEditor = new ActionEditorUI(action);
        actionEditor.previousScreen = cloneMenu();

        if (client == null) return;
        client.setScreen(actionEditor);
    }

    private void createActionButtons(FlowLayout parent) {
        // Clear all the old buttons.
        parent.clearChildren();

        // Loop through each action button and determine the row it is on.
        double actionsCount = ActionButtonDataHandler.actions.size();

        int curAction = 0;
        double rowSize = 5;

        if (actionsCount > 0) {
            for (int y = 0; y < Math.ceil(actionsCount / rowSize); y++) {
                // Create a horizontal layout for each row.
                FlowLayout buttonRowLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
                buttonRowLayout.horizontalAlignment(HorizontalAlignment.CENTER);

                for (int i = 0; i < rowSize; i++) {
                    if (curAction >= actionsCount) break;

                    // Create the action button.
                    ActionButtonData data = ActionButtonDataHandler.actions.get(curAction);
                    QuickMenuButton button = createActionButton(data);
                    buttonRowLayout.child(button);

                    curAction++;
                }

                parent.child(buttonRowLayout);
            }
        } else {
            // When no actions exist.
            LabelComponent label = Components.label(Text.translatable("menu.main.no_actions"));
            label
                    .horizontalTextAlignment(HorizontalAlignment.CENTER)
                    .verticalTextAlignment(VerticalAlignment.CENTER)
                    .horizontalSizing(Sizing.fill(100))
                    .margins(Insets.of(5, 0, 0, 0));

            parent.child(label);
        }
    }

    private QuickMenuButton createActionButton(ActionButtonData data) {
        // Create the button.
        QuickMenuButton button = new QuickMenuButton(data.icon, (buttonComponent) -> {
            // On left click.
            if (QuickMenu.CONFIG.closeOnKeyReleased()) return;
            pressButton(data);
        }, (quickMenuButton) -> {
            // On right click.
            if (editMode) {
                ActionButtonDataHandler.remove(data);
                MinecraftClient.getInstance().setScreen(cloneMenu());
            }
        });

        // For the close on release option.
        button.mouseEnter().subscribe(() -> hoveredData = data);
        button.mouseLeave().subscribe(() -> hoveredData = null);

        // Set up the buttons properties.
        StringBuilder actionsText = new StringBuilder();
        if (QuickMenu.CONFIG.showActionsInTooltip()) {
            // If the tooltip should contain the actions the button should run.
            data.actions.forEach(
                    (actionData) -> actionsText.append("\n").append(actionData.getString())
            );
        }

        MutableText tooltip = Text.literal(data.name).append(Text.literal(actionsText.toString()).formatted(Formatting.DARK_GRAY));

        button
                .margins(Insets.of(1, 1, 2, 2))
                .tooltip(tooltip);

        return button;
    }

    public void pressButton(ActionButtonData data) {
        if (editMode) {
            gotoActionEditor(data);
            return;
        }

        // Run the buttons actions.
        data.run();

        if (QuickMenu.CONFIG.closeOnAction()) close();
    }

    public MainUI cloneMenu() {
        // Create an instance of this menu with the same edit mode.
        MainUI clone = new MainUI();
        clone.editMode = editMode;

        return clone;
    }

    private void closeOnKeybindRelease(int mouseButton) {
        closeOnKeybindRelease(true, mouseButton, 0);
    }

    private void closeOnKeybindRelease(int keyCode, int scanCode) {
        closeOnKeybindRelease(false, keyCode, scanCode);
    }

    private void closeOnKeybindRelease(boolean mouse, int button, int button2) {
        if (editMode) return;
        if (!QuickMenu.CONFIG.closeOnKeyReleased()) return;

        // Make sure the keybind is correct.
        if (
                (mouse && ModKeybindings.menuOpenKeybinding.matchesMouse(button))
                || (!mouse && ModKeybindings.menuOpenKeybinding.matchesKey(button, button2))
        ) {
            if (hoveredData != null) {
                // Press the current button.
                pressButton(hoveredData);
            }

            close();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_E) {
            editMode = !editMode;
            updateEditorLayout();
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        closeOnKeybindRelease(keyCode, scanCode);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        closeOnKeybindRelease(button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
