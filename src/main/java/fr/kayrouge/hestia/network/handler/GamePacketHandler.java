package fr.kayrouge.hestia.network.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import fr.kayrouge.hera.util.type.game.TerritoryType;
import fr.kayrouge.hestia.Hestia;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.DataInputStream;
import java.io.IOException;

public class GamePacketHandler {

    public static boolean displayTroopsBar = false;
    public static double troopsBarValue = 0d;

    public static void handleTerritoryGame(DataInputStream in) throws IOException {
        TerritoryType type = TerritoryType.getById(in.readUnsignedByte());
        switch (type) {
            case VISIBLE -> displayTroopsBar = in.readBoolean();
            case SET -> {
                Hestia.LOGGER.info(troopsBarValue);
                troopsBarValue = in.readDouble();
                Hestia.LOGGER.info(troopsBarValue);
                displayTroopsBar = true;
            }
        }
    }

    @SubscribeEvent
    public void guiDisplay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if(!displayTroopsBar) return;
        Minecraft mc = Minecraft.getInstance();
        MatrixStack matrixStack = event.getMatrixStack();

        int screenWidth = event.getWindow().getScreenWidth();
        int screenHeight = mc.getWindow().getScreenHeight();

        int barWidth = 182;
        int barHeight = 10;
        int x = 5;
        int y = 5;

        double progress = troopsBarValue;

        AbstractGui.fill(matrixStack, x, y, x + barWidth, y + barHeight, 0xFF555555);

        int filledWidth = (int)(barWidth * progress);
        AbstractGui.fill(matrixStack, x, y, x + filledWidth, y + barHeight, 0xFF00FF00);
    }

}
