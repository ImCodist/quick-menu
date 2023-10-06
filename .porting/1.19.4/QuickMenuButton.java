package xyz.imcodist.ui.components;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
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
                new Identifier("quickmenu", "textures/switcher_buttons.png"),
                0, 0,
                64, 64
        ));
    }

    @Override
    public void draw(MatrixStack context, int mouseX, int mouseY, float partialTicks, float delta) {
        super.draw(context, mouseX, mouseY, partialTicks, delta);

        // Draw the item inside the button.
        if (itemIcon != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null) return;

            ItemRenderer itemRenderer = client.getItemRenderer();
            if (itemRenderer == null) return;

            double divide = 5.2;
            itemRenderer.renderGuiItemIcon(context, itemIcon, (int) (x() + (width() / divide)), (int) (y() + (height() / divide)));
        }
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_2) rightClick.accept(this);
        return super.onMouseDown(mouseX, mouseY, button);
    }
}
