package xyz.imcodist.ui;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Insets;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.imcodist.ui.surfaces.SwitcherSurface;

import java.awt.*;
import java.util.concurrent.Flow;

public class MainGameUI extends BaseOwoScreen<FlowLayout> {
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
        FlowLayout mainLayout = Containers.verticalFlow(Sizing.fixed(6*30), Sizing.fixed(6*19));
        mainLayout.surface(new SwitcherSurface(false));

        rootComponent.child(mainLayout);

        // Create header.
        FlowLayout headerLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(6*4));
        headerLayout
                .surface(new SwitcherSurface(true))
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        LabelComponent headerLabel = Components.label(Text.translatable("menu.main.title"));
        headerLabel.shadow(true);

        headerLayout.child(headerLabel);
        mainLayout.child(headerLayout);

        // Setup button grid.
        FlowLayout buttonFlowLayout = Containers.verticalFlow(Sizing.fill(100), Sizing.content());

        ScrollContainer<Component> buttonLayout = Containers.verticalScroll(Sizing.fill(100), Sizing.fill(80), buttonFlowLayout);
        buttonLayout.padding(Insets.of(2, 5, 2, 2));

        int buttons = 20;
        int rowSize = 5;
        for (int y = 0; y < Math.ceil((double) buttons / (double) rowSize); y++) {
            FlowLayout buttonRowLayout = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
            buttonRowLayout.horizontalAlignment(HorizontalAlignment.CENTER);

            for (int i = 0; i < rowSize; i++) {
                ButtonComponent button = Components.button(Text.empty(), (buttonComponent) -> {

                });

                button
                        .sizing(Sizing.fixed(26), Sizing.fixed(26))
                        .margins(Insets.of(1, 1, 2, 2));

                button.renderer(ButtonComponent.Renderer.texture(
                        new Identifier("quick-menu", "textures/switcher_buttons.png"),
                        0, 0,
                        64, 64
                ));

                buttonRowLayout.child(button);
            }

            buttonFlowLayout.child(buttonRowLayout);
        }

        mainLayout.child(buttonLayout);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
