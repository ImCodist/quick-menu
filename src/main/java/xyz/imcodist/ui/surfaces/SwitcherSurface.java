package xyz.imcodist.ui.surfaces;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.util.Drawer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class SwitcherSurface implements Surface {
    boolean isHeader = false;

    public SwitcherSurface(boolean header) {
        isHeader = header;
    }
    public SwitcherSurface() {}

    @Override
    public void draw(MatrixStack matrices, ParentComponent component) {
        int x = component.x();
        int y = component.y();
        int width = component.width();
        int height = component.height();
        int sourceX = (isHeader) ? 0 : 24;

        RenderSystem.setShaderTexture(0, new Identifier("quickmenu", "textures/switcher_textures.png"));

        if (!isHeader) RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);

//        drawRepeatingTexture(matrices, x + sideSize, y, sourceX + sideSize, 0, 1, sideSize, 52, 39, width - (sideSize * 2), sideSize);
//        drawRepeatingTexture(matrices, x + sideSize, y + height - sideSize, sourceX + sideSize, sideSize + 1, 1, sideSize, 52, 39, width - (sideSize * 2), sideSize);
//        drawRepeatingTexture(matrices, x, y + sideSize, sourceX, 6, sideSize, 1, 52, 39, sideSize, height - (sideSize * 2));
//        drawRepeatingTexture(matrices, x + width - sideSize, y + sideSize, sourceX + sideSize + 1, 6, sideSize, 1, 52, 39, sideSize, height - (sideSize * 2));

        drawNineSlicedTexture(matrices, x, y, width, height, sourceX, 0, 6, 6, 12, 12, 52, 50);

        if (!isHeader) RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public void drawNineSlicedTexture(MatrixStack matrices, int x, int y, int width, int height, int sourceX, int sourceY, int sideWidth, int sideHeight, int centerWidth, int centerHeight, int textureWidth, int textureHeight) {
        // TOP AND BOTTOM
        drawRepeatingTexture(matrices, x + sideWidth, y, sourceX + sideWidth, sourceY, centerWidth, sideHeight, textureWidth, textureHeight, width - (sideWidth * 2), sideHeight);
        drawRepeatingTexture(matrices, x + sideWidth, y + height - sideHeight, sourceX + sideWidth, sourceY + sideHeight + centerHeight, centerWidth, sideHeight, textureWidth, textureHeight, width - (sideWidth * 2), sideHeight);

        // LEFT AND RIGHT
        drawRepeatingTexture(matrices, x, y + sideHeight, sourceX, sourceY + sideHeight, sideWidth, centerHeight, textureWidth, textureHeight, sideWidth, height - (sideHeight * 2));
        drawRepeatingTexture(matrices, x + width - sideWidth, y + sideHeight, sourceX + sideWidth + centerWidth, sourceY + sideHeight, sideWidth, centerHeight, textureWidth, textureHeight, sideWidth, height - (sideHeight * 2));

        // CORNERS
        drawTexture(matrices, x, y, sourceX, sourceY, sideWidth, sideHeight, textureWidth, textureHeight);
        drawTexture(matrices, x + width - sideWidth, y, sourceX + sideWidth + centerWidth, sourceY, sideWidth, sideHeight, textureWidth, textureHeight);
        drawTexture(matrices, x, y + height - sideHeight, sourceX, sourceY + sideHeight + centerHeight, sideWidth, sideHeight, textureWidth, textureHeight);
        drawTexture(matrices, x + width - sideWidth, y + height - sideHeight, sourceX + sideWidth + centerWidth, sourceY + sideHeight + centerHeight, sideWidth, sideHeight, textureWidth, textureHeight);

        // CENTER
        drawRepeatingTexture(matrices, x + sideWidth, y + sideHeight, sourceX + sideWidth, sourceY + sideHeight, centerWidth, centerHeight, textureWidth, textureHeight, width - (sideWidth * 2), height - (sideHeight * 2));
    }

    public void drawTexture(MatrixStack matrices, int x, int y, int sourceX, int sourceY, int sourceWidth, int sourceHeight, int textureWidth, int textureHeight) {
        Drawer.drawTexture(matrices, x, y, sourceX, sourceY, sourceWidth, sourceHeight, textureWidth, textureHeight);
    }

    public void drawRepeatingTexture(MatrixStack matrices, int x, int y, int sourceX, int sourceY, int sourceWidth, int sourceHeight, int textureWidth, int textureHeight, int width, int height) {
        double xMax = (double) width / (double) sourceWidth;
        double yMax = (double) height / (double) sourceHeight;

        for (int xi = 0; xi < Math.ceil(xMax); xi++) {
            for (int yi = 0; yi < Math.ceil(yMax); yi++) {
                int newWidth = sourceWidth;
                int newHeight = sourceHeight;
                if (Math.floor(xMax) == xi) newWidth *= (double) (xMax - xi);
                if (Math.floor(yMax) == yi) newHeight *= (double) (yMax - yi);

                drawTexture(matrices, x + (sourceWidth * xi), y + (sourceHeight * yi), sourceX, sourceY, newWidth, newHeight, textureWidth, textureHeight);
            }
        }
    }
}
