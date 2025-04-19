package fr.kayrouge.hestia;

import com.mojang.datafixers.util.Pair;
import fr.kayrouge.hera.Choice;
import fr.kayrouge.hera.Hera;
import fr.kayrouge.hestia.screen.QuestionListScreen;
import fr.kayrouge.hestia.screen.QuestionScreen;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NetworkManager {

    @SubscribeEvent
    public void onClientJoin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        if(Minecraft.getInstance().getConnection() != null) {
            net.minecraft.network.NetworkManager manager = Minecraft.getInstance().getConnection().getConnection();

            manager.channel().pipeline().addBefore("packet_handler", "hermes:hestia", new SimpleChannelInboundHandler<SCustomPayloadPlayPacket>() {
                @Override
                protected void channelRead0(ChannelHandlerContext channelHandlerContext, SCustomPayloadPlayPacket o) {
                    if(o.getIdentifier().equals(new ResourceLocation("hermes", "hestia"))) {
                        ByteBuf directBuf = o.getData().copy();
                        ByteBuf heapBuf = Unpooled.copiedBuffer(directBuf);
                        directBuf.release();

                        DataInputStream in = new DataInputStream(new ByteArrayInputStream(heapBuf.array()));
                        try {
                            String sousCanal = in.readUTF();
                            PlayerEntity player = Minecraft.getInstance().player;
                            switch (sousCanal) {
                                case "join" -> {
                                    player.sendMessage(new StringTextComponent("Connected with Hermes, Hermes Hera version: " + in.readLong() + ", Hestia Hera version: " + Hera.VERSION), Util.NIL_UUID);

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    DataOutputStream out = new DataOutputStream(baos);

                                    try {
                                        out.writeUTF("heraVersion");
                                        out.writeLong(Hera.VERSION);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    PacketBuffer buffer = new PacketBuffer(Unpooled.wrappedBuffer(baos.toByteArray()));
                                    CCustomPayloadPacket packet = new CCustomPayloadPacket(new ResourceLocation("hermes", "hestia"), buffer);
                                    manager.send(packet);
                                }
                                case "question" -> {
                                    String question = in.readUTF();
                                    int questionId = in.readInt();
                                    int length = in.readInt();
                                    Choice[] choices = new Choice[length];
                                    for (int i = 0; i < length; i++) {
                                        choices[i] = Choice.fromPacket(in);
                                    }
                                    Minecraft.getInstance().setScreen(new QuestionScreen(question, questionId, choices));
                                }
                                case "questions" -> receivedQuestionList(in);
                            }
                        } catch (IOException e) {
                            Hestia.LOGGER.info("Error reading packet");
                            e.printStackTrace();
                        }
                        heapBuf.release();


                    }
                }
            });
        }
    }

    public void receivedQuestionList(DataInputStream in) throws IOException {
        int questionListSize = in.readInt();
        List<Pair<Integer, String>> questions = new ArrayList<>();
        for(int i = 0; i < questionListSize; i++) {
            int id = in.readInt();
            String name = in.readUTF();
            questions.add(new Pair<>(id, name));
            Hestia.LOGGER.info("{} {}", id, name);
        }

        Minecraft.getInstance().setScreen(new QuestionListScreen(questions));
    }
}
