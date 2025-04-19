package fr.kayrouge.hestia;

import fr.kayrouge.hera.Choice;
import fr.kayrouge.hestia.screen.QuestionScreen;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Keybindings {

    public static final KeyBinding QUESTION_TEST = new KeyBinding("key.question", KeyEvent.VK_M, "key.categories.test");
    public static final KeyBinding QUESTION_LIST_TEST = new KeyBinding("key.questionlist", KeyEvent.VK_N, "key.categories.test");

    public Keybindings() {
        ClientRegistry.registerKeyBinding(QUESTION_TEST);
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) throws IOException {
        while (QUESTION_TEST.isDown()) {
            Choice[] choices = new Choice[] {Choice.of("cancel"), Choice.of("text", Choice.Type.TEXT_ENTRY)};
            Minecraft.getInstance().setScreen(new QuestionScreen("TEST (CLIENT ONLY)", -1, choices));
        }

        while (QUESTION_LIST_TEST.isDown()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.writeUTF("questions");
            PacketBuffer buffer = new PacketBuffer(Unpooled.wrappedBuffer(baos.toByteArray()));
            CCustomPayloadPacket packet = new CCustomPayloadPacket(new ResourceLocation("hermes", "hestia"), buffer);
            if(Minecraft.getInstance().getConnection() != null) {
                Minecraft.getInstance().getConnection().send(packet);
            }
            else {
                Hestia.LOGGER.error("Connection is null in {}", this.getClass().toGenericString());
            }
        }
    }
}
