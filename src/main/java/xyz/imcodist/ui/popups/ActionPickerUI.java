package xyz.imcodist.ui.popups;

import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.item.ItemStack;
import xyz.imcodist.data.command_actions.BaseActionData;

import java.util.function.Consumer;

public class ActionPickerUI extends OverlayContainer<FlowLayout> {
    public Consumer<BaseActionData> onSelectedAction;

    public ActionPickerUI() {
        super(Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100)));
        FlowLayout rootComponent = child;

        // Setup root.
        rootComponent
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);
    }
}
