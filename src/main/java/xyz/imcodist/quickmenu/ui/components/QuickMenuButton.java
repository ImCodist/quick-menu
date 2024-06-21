package xyz.imcodist.quickmenu.ui.components;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class QuickMenuButton extends ButtonComponent {
    public ItemStack itemIcon;

    public Consumer<QuickMenuButton> rightClick;

    public QuickMenuButton(ItemStack icon, Consumer<ButtonComponent> onPress, Consumer<QuickMenuButton> onRightClick) {
        super(Text.empty(), onPress);

        itemIcon = icon;
        rightClick = onRightClick;

        sizing(Sizing.fixed(26), Sizing.fixed(26));
        renderer(ButtonComponent.Renderer.texture(
                Identifier.of("quickmenu", "textures/switcher_buttons.png"),
                0, 0,
                64, 64
        ));
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        super.draw(context, mouseX, mouseY, partialTicks, delta);

        // Draw the item inside the button.
        if (itemIcon != null) {
            double divide = 5.2;
            context.drawItem(itemIcon, (int) (x() + (width() / divide)), (int) (y() + (height() / divide)));
        }
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_2) rightClick.accept(this);
        return super.onMouseDown(mouseX, mouseY, button);
    }
}
