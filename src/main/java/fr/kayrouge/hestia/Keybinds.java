package fr.kayrouge.hestia;

import fr.kayrouge.hera.Choice;
import fr.kayrouge.hestia.screen.QuestionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.awt.event.KeyEvent;

public class Keybinds {

    public static final KeyBinding QUESTION_TEST = new KeyBinding("key.question", KeyEvent.VK_M, "key.categories.test");

    public Keybinds() {
        ClientRegistry.registerKeyBinding(QUESTION_TEST);
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        while (QUESTION_TEST.isDown()) {
            Choice[] choices = new Choice[] {Choice.of("cancel"), Choice.of("text", Choice.Type.TEXT_ENTRY)};
            Minecraft.getInstance().setScreen(new QuestionScreen("TEST (CLIENT ONLY)", -1, choices));
        }
    }
}
