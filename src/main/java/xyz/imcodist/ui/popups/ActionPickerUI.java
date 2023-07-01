package xyz.imcodist.ui.popups;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.imcodist.data.command_actions.BaseActionData;
import xyz.imcodist.data.command_actions.CommandActionData;
import xyz.imcodist.data.command_actions.KeybindActionData;
import xyz.imcodist.ui.surfaces.SwitcherSurface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ActionPickerUI extends OverlayContainer<FlowLayout> {
    public Consumer<BaseActionData> onSelectedAction;

    private final ArrayList<ArrayList<String>> listData = new ArrayList<>();

    public ActionPickerUI() {
        super(Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100)));
        FlowLayout rootComponent = child;

        // Set up root.
        rootComponent
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        // Set up the main layout.
        FlowLayout mainLayout = Containers.verticalFlow(Sizing.fixed(180), Sizing.fixed(180));
        mainLayout
                .surface(new SwitcherSurface())
                .padding(Insets.of(10));
        rootComponent.child(mainLayout);

        // Set up the scroll layout.
        FlowLayout actionsLayout = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        ScrollContainer<FlowLayout> itemsScrollContainer = Containers.verticalScroll(Sizing.fill(100), Sizing.fill(100), actionsLayout);

        mainLayout.child(itemsScrollContainer);

        // Create action buttons.
        addListData("command", "Command", "Runs a chat command.");
        addListData("keybind", "Keybind", "Activates a keybind.");

        for (ArrayList<String> data : listData) {
            actionsLayout.child(createActionLayout(data));
        }
    }

    private void addListData(String type, String name, String description) {
        listData.add(new ArrayList<>(List.of(type, name, description)));
    }

    private FlowLayout createActionLayout(ArrayList<String> data) {
        String type = data.get(0);
        String name = data.get(1);
        String description = data.get(2);

        // Create the main layout.
        FlowLayout layout = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
        layout
                .padding(Insets.of(0, 8, 0, 4))
                .alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER);

        // Add the label.
        MutableText labelText = Text.literal(name).append("\n").append(Text.literal(description).formatted(Formatting.ITALIC));
        LabelComponent label = Components.label(labelText);
        label.positioning(Positioning.relative(0, 50));

        layout.child(label);

        // Add the add button.
        ButtonComponent addButton = Components.button(Text.literal(" + "), (buttonComponent) -> {
            onSelectedAction.accept(getActionFromType(type));
            remove();
        });
        addButton.margins(Insets.of(0, 0, 0, 0));

        layout.child(addButton);

        return layout;
    }

    private BaseActionData getActionFromType(String type) {
        switch (type) {
            case "command" -> {return new CommandActionData();}
            case "keybind" -> {return new KeybindActionData();}
        }

        return null;
    }
}
