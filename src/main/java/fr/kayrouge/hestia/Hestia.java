package fr.kayrouge.hestia;

import fr.kayrouge.hera.Hera;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("hestia")
public class Hestia
{
    public static final Logger LOGGER = LogManager.getLogger();

    public Hestia() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLClientSetupEvent event)
    {
        LOGGER.info("Using Hera "+ Hera.VERSION);
        MinecraftForge.EVENT_BUS.register(new NetworkManager());
        MinecraftForge.EVENT_BUS.register(new Keybinds());
    }
}
