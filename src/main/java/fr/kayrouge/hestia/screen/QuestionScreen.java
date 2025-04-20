package fr.kayrouge.hestia.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import fr.kayrouge.hera.Choice;
import fr.kayrouge.hera.util.PacketUtils;
import fr.kayrouge.hera.util.type.PacketType;
import fr.kayrouge.hera.util.type.QuestionsType;
import fr.kayrouge.hestia.Hestia;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class QuestionScreen extends Screen {

    private final String message;
    private final int questionId;
    private final Choice[] choices;
    private Choice choice;
    private Object data;


    public QuestionScreen(String message, int id, Choice[] choices) {
        super(new StringTextComponent("Question"));
        this.message = message;
        this.questionId = id;
        this.choices = choices;
        if(choices.length != 0) this.choice = choices[0];
        Hestia.LOGGER.info("Open question screen for question '{}' with id: {}", message, id);
    }

    @Override
    protected void init() {
        int yOffset = 0;
        for (Choice choice : this.choices) {
            switch (choice.getType()) {
                case SIMPLE_BUTTON ->
                        this.addButton(new Button(this.width/2-50, this.height/2-yOffset, 100, 20, new StringTextComponent(choice.getName()), button -> {
                            this.choice = choice;
                            close();
                        }));
                case TEXT_ENTRY -> {
                    TextFieldWidget textFieldWidget = this.addWidget(new TextFieldWidget(font, this.width/2-50, this.height/2-yOffset, 80, 20, new StringTextComponent(choice.getName())));
                    this.addButton(new Button(this.width/2+30, this.height/2-yOffset, 100, 20, new StringTextComponent("Send"), button -> {
                        this.choice = choice;
                        this.data = textFieldWidget.getValue();
                        close();
                    }));
                }
            }


            yOffset+=25;

        }
    }

    private void answer(Choice choice) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);

        try {
            out.writeByte(PacketType.QUESTION.getId());
            out.writeByte(QuestionsType.ANSWER.getId());
            out.writeInt(this.questionId);
            out.writeUTF(choice.getName());
            PacketUtils.writeObject(out, this.data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PacketBuffer buffer = new PacketBuffer(Unpooled.wrappedBuffer(baos.toByteArray()));
        CCustomPayloadPacket packet = new CCustomPayloadPacket(new ResourceLocation("hermes", "hestia"), buffer);
        if(getMinecraft().getConnection() != null) {
            getMinecraft().getConnection().send(packet);
        }
        else {
            Hestia.LOGGER.error("Connection is null in {}", this.getClass().toGenericString());
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float p_230430_4_) {
        //renderBackground(matrices);

        drawCenteredString(matrices, getMinecraft().font, this.message, this.width/2, this.height/6, Color.WHITE.getRGB());

        super.render(matrices, mouseX, mouseY, p_230430_4_);
        for (IGuiEventListener child : this.children) {
            if (!(child instanceof Button)) {
                ((Widget) child).render(matrices, mouseX, mouseY, p_230430_4_);
            }
        }
    }


    @Override
    public void onClose() {
        super.onClose();
        if(this.choice != null) {
            answer(choice);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void close() {
        this.minecraft.setScreen(null);
        this.minecraft.mouseHandler.grabMouse();
        if(this.choice != null) {
            answer(choice);
        }
    }


}
