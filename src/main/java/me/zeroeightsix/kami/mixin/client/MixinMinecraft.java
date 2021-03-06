package me.zeroeightsix.kami.mixin.client;

import me.zeroeightsix.kami.event.KamiEventBus;
import me.zeroeightsix.kami.event.events.GuiScreenEvent;
import me.zeroeightsix.kami.module.modules.combat.CrystalAura;
import me.zeroeightsix.kami.module.modules.misc.DiscordRPC;
import me.zeroeightsix.kami.util.ConfigUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by 086 on 17/11/2017.
 */
@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Shadow public WorldClient world;
    @Shadow public EntityPlayerSP player;
    @Shadow public GuiScreen currentScreen;
    @Shadow public GameSettings gameSettings;
    @Shadow public GuiIngame ingameGUI;
    @Shadow public boolean skipRenderWorld;
    @Shadow public SoundHandler soundHandler;
    @Shadow public RayTraceResult objectMouseOver;
    @Shadow public PlayerControllerMP playerController;
    @Shadow public EntityRenderer entityRenderer;

    @Inject(method = "rightClickMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;getHeldItem(Lnet/minecraft/util/EnumHand;)Lnet/minecraft/item/ItemStack;"), cancellable = true)
    public void processRightClickBlock(CallbackInfo ci) {
        if (CrystalAura.INSTANCE.isActive()) {
            ci.cancel();
            for (EnumHand enumhand : EnumHand.values()) {
                ItemStack itemstack = this.player.getHeldItem(enumhand);
                if (itemstack.isEmpty() && (this.objectMouseOver == null || this.objectMouseOver.typeOfHit == RayTraceResult.Type.MISS))
                    net.minecraftforge.common.ForgeHooks.onEmptyClick(this.player, enumhand);
                if (!itemstack.isEmpty() && this.playerController.processRightClick(this.player, this.world, enumhand) == EnumActionResult.SUCCESS) {
                    this.entityRenderer.itemRenderer.resetEquippedProgress(enumhand);
                }
            }
        }
    }

    @ModifyVariable(method = "displayGuiScreen", at = @At("HEAD"), argsOnly = true)
    public GuiScreen editDisplayGuiScreen(GuiScreen guiScreenIn) {
        GuiScreenEvent.Closed screenEvent = new GuiScreenEvent.Closed(this.currentScreen);
        KamiEventBus.INSTANCE.post(screenEvent);
        GuiScreenEvent.Displayed screenEvent1 = new GuiScreenEvent.Displayed(guiScreenIn);
        KamiEventBus.INSTANCE.post(screenEvent1);
        return screenEvent1.getScreen();
    }

    @Inject(method = "run", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V", shift = At.Shift.BEFORE))
    public void displayCrashReport(CallbackInfo _info) {
        save();
        DiscordRPC.INSTANCE.end();
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    public void shutdown(CallbackInfo info) {
        save();
        DiscordRPC.INSTANCE.end();
    }

    private void save() {
        System.out.println("Shutting down: saving NECRON configuration");
        ConfigUtils.INSTANCE.saveAll();
        System.out.println("Configuration saved.");
    }

}

