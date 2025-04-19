package fr.kayrouge.hestia.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import fr.kayrouge.hestia.Hestia;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.util.List;

public class QuestionListScreen extends Screen {

    private final List<Pair<Integer, String>> questions;

    public QuestionListScreen(List<Pair<Integer, String>> questions) {
        super(new StringTextComponent("Questions List"));
        this.questions = questions;
    }

    @Override
    public void init() {
        int offset = 0;
        for(Pair<Integer, String> question : questions) {
            this.addButton(new Button(this.width/2-50, this.height/4+offset, 100, 20, new StringTextComponent(question.getSecond()), button -> {
                Hestia.LOGGER.info("Clicked {} | id: {}", question.getSecond(), question.getFirst());
            }));
            offset += font.lineHeight+5;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float p_230430_4_) {
        super.render(matrices, mouseX, mouseY, p_230430_4_);

        if(this.questions.isEmpty()) {
            drawCenteredString(matrices, font, "No question to display", this.width/2, this.height/2, Color.WHITE.getRGB());
        }
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
