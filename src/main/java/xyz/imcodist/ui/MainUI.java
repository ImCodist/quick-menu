package xyz.imcodist.ui;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import xyz.imcodist.QuickMenu;
import xyz.imcodist.data.ActionData;
import xyz.imcodist.other.ActionDataHandler;
import xyz.imcodist.ui.components.QuickMenuButton;
import xyz.imcodist.ui.surfaces.SwitcherSurface;

public class MainUI extends BaseOwoScreen<FlowLayout> {
    public boolean editMode = false;

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
        FlowLayout editorLayout = Containers.horizontalFlow(Sizing.fixed(0), Sizing.fixed(0));
        editorLayout.padding(Insets.of(6));
        rootComponent.child(editorLayout);

        // Create header.
        int headerLayoutHeight = 6*4;
        FlowLayout headerLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(headerLayoutHeight));
        headerLayout
                .surface(new SwitcherSurface(true))
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);
        mainLayout.child(headerLayout);

        LabelComponent headerLabel = Components.label(Text.translatable("menu.main.title"));
        headerLabel
                .shadow(true);
        headerLayout.child(headerLabel);

        ButtonComponent headerEditButton = Components.button(Text.literal("✎"), (buttonComponent) -> {
            editMode = !editMode;
            updateEditorLayout(editorLayout);
        });
        headerEditButton
                .textShadow(true)
                .renderer(ButtonComponent.Renderer.flat(0x000000, 0x000000, 0x000000));
        headerLayout.child(headerEditButton);

        // Setup action layouts.
        FlowLayout actionFlowLayout = Containers.verticalFlow(Sizing.fill(100), Sizing.content());

        int actionLayoutHeight = mainLayoutHeight - headerLayoutHeight;
        ScrollContainer<Component> actionLayout = Containers.verticalScroll(Sizing.fill(100), Sizing.fixed(actionLayoutHeight), actionFlowLayout);
        actionLayout.padding(Insets.of(2, 5, 2, 2));
        mainLayout.child(actionLayout);

        // Create action buttons.
        createActionButtons(actionFlowLayout);

        // Editor controls.
        ButtonComponent newButton = Components.button(Text.translatable("menu.main.button.add_action"), (buttonComponent) -> {
            gotoActionEditor(null);
        });
        editorLayout.child(newButton);

        // Hide or show the editor layout depending on editing or not.
        updateEditorLayout(editorLayout);
    }

    private void updateEditorLayout(FlowLayout editorLayout) {
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

    private void gotoActionEditor(ActionData action) {
        ActionEditorUI actionEditor = new ActionEditorUI(action);
        actionEditor.previousScreen = cloneMenu();

        MinecraftClient.getInstance().setScreen(actionEditor);
    }

    private void createActionButtons(FlowLayout parent) {
        parent.clearChildren();

        int actions = ActionDataHandler.actions.size();

        int curAction = 0;
        int curRow = 0;
        int rowSize = 5;

        if (actions > 0) {
            for (int y = 0; y < Math.ceil((double) actions / (double) rowSize); y++) {
                FlowLayout buttonRowLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
                buttonRowLayout.horizontalAlignment(HorizontalAlignment.CENTER);

                for (int i = 0; i < rowSize; i++) {
                    if (curAction >= actions) break;

                    // Create the action button.
                    ActionData data = ActionDataHandler.actions.get(curAction);
                    QuickMenuButton button = createActionButton(data, parent);
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

    private QuickMenuButton createActionButton(ActionData data, FlowLayout parent) {
        // Create the button.
        QuickMenuButton button = new QuickMenuButton(data.icon, (buttonComponent) -> {
            // On click.
            MinecraftClient client = MinecraftClient.getInstance();

            ClientPlayerEntity player = client.player;
            if (player == null) return;

            if (editMode) {
                gotoActionEditor(data);
                return;
            }

            // Run the buttons action.
            String commandToRun = data.action;
            if (commandToRun != null) {
                if (commandToRun.startsWith("/")) {
                    commandToRun = commandToRun.substring(1);
                    player.networkHandler.sendChatCommand(commandToRun);
                } else {
                    player.networkHandler.sendChatMessage(commandToRun);
                }
            }

            if (QuickMenu.CONFIG.closeOnAction()) close();
        }, (quickMenuButton) -> {
            // On right click.
            if (editMode) {
                ActionDataHandler.remove(data);
                createActionButtons(parent);
            }
        });

        // Setup the buttons properties.
        button
                .margins(Insets.of(1, 1, 2, 2))
                .tooltip(Text.literal(data.name));

        return button;
    }

    public MainUI cloneMenu() {
        MainUI clone = new MainUI();
        clone.editMode = editMode;

        return clone;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
