package xyz.imcodist.ui;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import xyz.imcodist.QuickMenu;
import xyz.imcodist.data.ButtonData;
import xyz.imcodist.data.ButtonDataHandler;
import xyz.imcodist.ui.components.QuickMenuButton;
import xyz.imcodist.ui.surfaces.SwitcherSurface;

public class MainGameUI extends BaseOwoScreen<FlowLayout> {
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
        FlowLayout mainLayout = Containers.verticalFlow(
                Sizing.fixed(QuickMenu.CONFIG.menuWidth()),
                Sizing.fixed(QuickMenu.CONFIG.menuHeight())
        );
        mainLayout.surface(new SwitcherSurface(false));

        rootComponent.child(mainLayout);

        // Create header.
        FlowLayout headerLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(6*4));
        headerLayout
                .surface(new SwitcherSurface(true))
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        LabelComponent headerLabel = Components.label(Text.translatable("menu.main.title"));
        headerLabel
                .shadow(true);

        ButtonComponent headerEditButton = Components.button(Text.literal("✎"), (buttonComponent) -> {
            editMode = !editMode;

            Text newText = Text.translatable("menu.main.title");
            if (editMode) newText = Text.translatable("menu.main.title").append(Text.literal(" (Edit Mode)"));

            headerLabel.text(newText);
        });
        headerEditButton
                .textShadow(true)
                .renderer(ButtonComponent.Renderer.flat(0x000000, 0x000000, 0x000000));

        headerLayout.child(headerLabel);
        headerLayout.child(headerEditButton);

        mainLayout.child(headerLayout);

        // Setup button grid.
        FlowLayout buttonFlowLayout = Containers.verticalFlow(Sizing.fill(100), Sizing.content());

        ScrollContainer<Component> buttonLayout = Containers.verticalScroll(Sizing.fill(100), Sizing.fill(80), buttonFlowLayout);
        buttonLayout.padding(Insets.of(2, 5, 2, 2));

        // Create buttons.
        int buttons = ButtonDataHandler.buttons.size();

        int curButton = 0;
        int rowSize = 5;

        if (buttons > 0) {
            for (int y = 0; y < Math.ceil((double) buttons / (double) rowSize); y++) {
                FlowLayout buttonRowLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
                buttonRowLayout.horizontalAlignment(HorizontalAlignment.CENTER);

                for (int i = 0; i < rowSize; i++) {
                    if (curButton >= buttons) {
                        break;
                    }

                    ButtonData data = ButtonDataHandler.buttons.get(curButton);

                    ButtonComponent button = new QuickMenuButton(Text.empty(), data.icon, (buttonComponent) -> {
                        MinecraftClient client = MinecraftClient.getInstance();

                        ClientPlayerEntity player = client.player;
                        if (player == null) return;

                        if (editMode) {
                            player.sendMessage(Text.literal("Edit"));
                            return;
                        }

                        String commandToRun = data.action;
                        if (commandToRun.startsWith("/")) {
                            commandToRun = commandToRun.substring(1);
                            player.networkHandler.sendChatCommand(commandToRun);
                        } else {
                            player.networkHandler.sendChatMessage(commandToRun);
                        }
                    });

                    button
                            .margins(Insets.of(1, 1, 2, 2))
                            .tooltip(Text.literal(data.name));


                    buttonRowLayout.child(button);

                    curButton++;
                }

                buttonFlowLayout.child(buttonRowLayout);
            }
        } else {
            LabelComponent label = Components.label(Text.literal("No buttons created."));
            label
                    .horizontalTextAlignment(HorizontalAlignment.CENTER)
                    .verticalTextAlignment(VerticalAlignment.CENTER)
                    .horizontalSizing(Sizing.fill(100));

            buttonFlowLayout.child(label);
        }

        mainLayout.child(buttonLayout);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
