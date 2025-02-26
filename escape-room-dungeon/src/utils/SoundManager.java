package utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

public class SoundManager {

  public static float volume = 0.5f; //50%


  public static void playSound(Sounds sound, float minPitch, float maxPitch){
    Sound soundEffect = Gdx.audio.newSound(Gdx.files.internal(sound.getPath()));
    long soundId = soundEffect.play();
    float randomPitch = MathUtils.random(minPitch, maxPitch);
    soundEffect.setPitch(soundId, randomPitch);
    soundEffect.setVolume(soundId, 0.5f * volume);
  }
  public static void playSound(Sounds sound){
    playSound(sound, 1, 1);
  }

  public static void playOneOf(Sounds[] sounds){
    if(sounds.length == 0) throw new IllegalArgumentException("sounds is empty");
    Sounds selected = sounds[(int)(Math.random() * sounds.length)];
    playSound(sounds[0]);
  }

}
