package xyz.imcodist.quickmenu.ui.surfaces;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.util.Identifier;

public class SwitcherSurface implements Surface {
    boolean isHeader = false;

    public SwitcherSurface(boolean header) {
        isHeader = header;
    }
    public SwitcherSurface() {}

    @Override
    public void draw(OwoUIDrawContext context, ParentComponent component) {
        int x = component.x();
        int y = component.y();
        int width = component.width();
        int height = component.height();

        int sourceX = (isHeader) ? 0 : 24;

        // Make sure the background renders as transparent.
        if (!isHeader) RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        // Draws the texture as a 9 slice.
        drawNineSlicedTexture(context, x, y, width, height, sourceX, 0, 6, 6, 12, 12, 52, 50);

        // Undo previous render system changes.
        if (!isHeader) RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public void drawNineSlicedTexture(OwoUIDrawContext context, int x, int y, int width, int height, int sourceX, int sourceY, int sideWidth, int sideHeight, int centerWidth, int centerHeight, int textureWidth, int textureHeight) {
        // before someone goes and tells me this method was useless to make i got
        // so frustrated trying to use the minecraft nine slice method but hated it a lot

        // TOP AND BOTTOM
        drawRepeatingTexture(context, x + sideWidth, y, sourceX + sideWidth, sourceY, centerWidth, sideHeight, textureWidth, textureHeight, width - (sideWidth * 2), sideHeight);
        drawRepeatingTexture(context, x + sideWidth, y + height - sideHeight, sourceX + sideWidth, sourceY + sideHeight + centerHeight, centerWidth, sideHeight, textureWidth, textureHeight, width - (sideWidth * 2), sideHeight);

        // LEFT AND RIGHT
        drawRepeatingTexture(context, x, y + sideHeight, sourceX, sourceY + sideHeight, sideWidth, centerHeight, textureWidth, textureHeight, sideWidth, height - (sideHeight * 2));
        drawRepeatingTexture(context, x + width - sideWidth, y + sideHeight, sourceX + sideWidth + centerWidth, sourceY + sideHeight, sideWidth, centerHeight, textureWidth, textureHeight, sideWidth, height - (sideHeight * 2));

        // CORNERS
        drawTexture(context, x, y, sourceX, sourceY, sideWidth, sideHeight, textureWidth, textureHeight);
        drawTexture(context, x + width - sideWidth, y, sourceX + sideWidth + centerWidth, sourceY, sideWidth, sideHeight, textureWidth, textureHeight);
        drawTexture(context, x, y + height - sideHeight, sourceX, sourceY + sideHeight + centerHeight, sideWidth, sideHeight, textureWidth, textureHeight);
        drawTexture(context, x + width - sideWidth, y + height - sideHeight, sourceX + sideWidth + centerWidth, sourceY + sideHeight + centerHeight, sideWidth, sideHeight, textureWidth, textureHeight);

        // CENTER
        drawRepeatingTexture(context, x + sideWidth, y + sideHeight, sourceX + sideWidth, sourceY + sideHeight, centerWidth, centerHeight, textureWidth, textureHeight, width - (sideWidth * 2), height - (sideHeight * 2));
    }

    public void drawTexture(OwoUIDrawContext context, int x, int y, int sourceX, int sourceY, int sourceWidth, int sourceHeight, int textureWidth, int textureHeight) {
        context.drawTexture(new Identifier("quickmenu", "textures/switcher_textures.png"), x, y, sourceX, sourceY, sourceWidth, sourceHeight, textureWidth, textureHeight);
    }

    public void drawRepeatingTexture(OwoUIDrawContext context, int x, int y, int sourceX, int sourceY, int sourceWidth, int sourceHeight, int textureWidth, int textureHeight, int width, int height) {
        double xMax = (double) width / (double) sourceWidth;
        double yMax = (double) height / (double) sourceHeight;

        for (int xi = 0; xi < Math.ceil(xMax); xi++) {
            for (int yi = 0; yi < Math.ceil(yMax); yi++) {
                int newWidth = sourceWidth;
                int newHeight = sourceHeight;
                if (Math.floor(xMax) == xi) newWidth *= (double) (xMax - xi);
                if (Math.floor(yMax) == yi) newHeight *= (double) (yMax - yi);

                drawTexture(context, x + (sourceWidth * xi), y + (sourceHeight * yi), sourceX, sourceY, newWidth, newHeight, textureWidth, textureHeight);
            }
        }
    }
}
