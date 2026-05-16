package guivnf.sanity_renewed.sound;

import guivnf.sanity_renewed.config.ConfigProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

/**
 * Tickable sound instances used by {@link guivnf.sanity_renewed.client.SanityAmbience}
 * to loop the heartbeat / insanity drone, plus a one-shot swish.
 */
public final class SanitySoundInstances
{
    private SanitySoundInstances() {}

    public static void playSwish()
    {
        Minecraft.getInstance().getSoundManager().play(
                new AbstractSoundInstance(SoundRegistry.SWISH.get(), SoundSource.AMBIENT, RandomSource.create()) {});
    }

    public static final class Heartbeat extends AbstractTickableSoundInstance
    {
        public float factor;

        public Heartbeat()
        {
            super(SoundRegistry.HEARTBEAT.get(), SoundSource.AMBIENT, RandomSource.create());
            this.volume = 0f;
            this.delay = 0;
            this.looping = true;
        }

        @Override
        public boolean canStartSilent() { return true; }

        @Override
        public void tick()
        {
            Minecraft mc = Minecraft.getInstance();
            volume = mc.player == null ? 0f
                    : factor * ConfigProxy.getInsanityVolume(mc.player.level().dimension().location());
        }

        public void setEyePos(Vec3 pos)
        {
            this.x = pos.x; this.y = pos.y; this.z = pos.z;
        }

        public void stopNow() { stop(); }
    }

    public static final class Insanity extends AbstractTickableSoundInstance
    {
        public float factor;

        public Insanity()
        {
            super(SoundRegistry.INSANITY.get(), SoundSource.AMBIENT, RandomSource.create());
            this.volume = 0f;
            this.delay = 0;
            this.looping = true;
        }

        @Override
        public boolean canStartSilent() { return true; }

        @Override
        public void tick()
        {
            Minecraft mc = Minecraft.getInstance();
            volume = mc.player == null ? 0f
                    : factor * ConfigProxy.getInsanityVolume(mc.player.level().dimension().location());
        }

        public void setEyePos(Vec3 pos)
        {
            this.x = pos.x; this.y = pos.y; this.z = pos.z;
        }

        public void stopNow() { stop(); }
    }
}
