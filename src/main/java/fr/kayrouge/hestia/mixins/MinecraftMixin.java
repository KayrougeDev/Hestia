package fr.kayrouge.hestia.mixins;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "allowsMultiplayer", at = @At("RETURN"), cancellable = true)
    public void allowsMultiplayer(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(method = "allowsChat", at = @At("RETURN"), cancellable = true)
    public void allowsChat(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

}
