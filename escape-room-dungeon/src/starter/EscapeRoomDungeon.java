package starter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import contrib.components.InventoryComponent;
import contrib.components.UIComponent;
import contrib.configuration.KeyboardConfig;
import contrib.crafting.Crafting;
import contrib.entities.HeroFactory;
import contrib.entities.MiscFactory;
import contrib.entities.MonsterFactory;
import contrib.hud.DialogUtils;
import contrib.hud.elements.GUICombination;
import contrib.hud.inventory.InventoryGUI;
import contrib.item.HealthPotionType;
import contrib.item.concreteItem.ItemPotionHealth;
import contrib.systems.*;
import contrib.utils.components.Debugger;
import contrib.utils.components.interaction.InteractionTool;
import contrib.utils.components.item.ItemGenerator;
import contrib.utils.components.skill.SkillTools;
import core.Entity;
import core.Game;
import core.System;
import core.components.PlayerComponent;
import core.game.GameLoop;
import core.systems.LevelSystem;
import core.utils.Point;
import core.utils.Tuple;
import core.utils.components.path.SimpleIPath;
import entities.BurningFireballSkill;
import hud.DebugOverlay;
import item.concreteItem.ItemPotionWater;
import item.concreteItem.ItemResourceBerry;
import item.concreteItem.ItemResourceMushroomRed;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Comparator;
import java.util.logging.*;

import level.utils.DungeonLoader;
import modules.keypad.KeypadSystem;
import systems.*;
import systems.EventScheduler;
import utils.GameState;

/**
 * Starter class for the DevDungeon game.
 */
public class EscapeRoomDungeon {

  public static final Logger LOGGER = Logger.getLogger(EscapeRoomDungeon.class.getName());
  private static final String BACKGROUND_MUSIC = "sounds/background.wav";
  private static final boolean ENABLE_CHEATS = false;
  private static final boolean SKIP_INTRO = true;
  private static DebugOverlay DEBUG_OVERLAY;

  /**
   * Main method to start the game.
   *
   * @param args The arguments passed to the game.
   * @throws IOException If an I/O error occurs.
   */
  public static void main(String[] args) throws IOException {
    Game.initBaseLogger(Level.WARNING);
    setupLogger();

    configGame();
    onSetup();
    GameState.initialLoadState();

    Game.userOnExit(GameState.INSTANCE::onBeforeApplicationExit);
    Game.userOnLevelLoad(
      (firstTime) -> {
        EventScheduler.getInstance().clear();
      });

    Game.run();
  }

  private static void setupLogger(){
    LOGGER.setLevel(Level.ALL);
    StreamHandler handler = new StreamHandler(java.lang.System.out, new SimpleFormatter()){
      @Override
      public void publish(LogRecord record){
        super.publish(record);
        flush();
      }
      @Override
      public void close(){
        flush();
      }
    };
    LOGGER.addHandler(handler);

    try{
      FileHandler fileHandler = new FileHandler("escape_room.log");
      fileHandler.setFormatter(new SimpleFormatter());
      LOGGER.addHandler(fileHandler);
    }catch(IOException ignored){}
  }

  private static void onSetup() {
    Game.userOnSetup(
      () -> {
        LevelSystem levelSystem = Game.getSystem(LevelSystem.class);
        levelSystem.onEndTile(DungeonLoader::loadNextLevel);

        createSystems();

        HeroFactory.setHeroSkillCallback(new BurningFireballSkill(SkillTools::cursorPositionAsPoint)); // Override default skill
        try {
          createHero();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        setupMusic();

        DEBUG_OVERLAY = new DebugOverlay();
        TickableSystem.register(DEBUG_OVERLAY, TickableSystem.TIMING_LAST);

        DungeonLoader.loadLevel(DungeonLoader.LevelLabel.MainMenu, 0);
        if(!SKIP_INTRO){
          TransitionSystem.openingTransition("Escape Room Dungeon");
        }
      });
  }

  private static void createHero() throws IOException {
    Entity hero = HeroFactory.newHero();
    PlayerComponent pc = hero.fetchOrThrow(PlayerComponent.class);
    //Overwrite close UI and interact binds
    pc.registerCallback(KeyboardConfig.CLOSE_UI.value(), EscapeRoomDungeon::onPressedCloseUI, false, true);
    pc.registerCallback(KeyboardConfig.INTERACT_WORLD.value(), EscapeRoomDungeon::onPressedInteract, false, true);
    Game.add(hero);
  }

  private static void setupMusic() {
    Music backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(BACKGROUND_MUSIC));
    backgroundMusic.setLooping(true);
    backgroundMusic.play();
    backgroundMusic.setVolume(.05f);
  }

  private static void configGame() throws IOException {
    Game.loadConfig(
      new SimpleIPath("dungeon_config.json"),
      contrib.configuration.KeyboardConfig.class,
      core.configuration.KeyboardConfig.class);
    Game.frameRate(60);
    Game.disableAudio(true);
    Game.windowTitle("Escape Room");
  }

  private static void createSystems() {
    Game.add(new ProjectileSystem());
    Game.add(new HudSystem());

    Game.add(EventScheduler.getInstance());

    Game.add(new TickableSystem());
    Game.add(new LeverSystem());
    Game.add(new DrawTextSystem());
    Game.add(new LevelEditorSystem());
    Game.add(new VicinitySystem());
    Game.add(new TransitionSystem());
    Game.add(new KeypadSystem());
  }

  private static void enableCheats() {
    Game.add(
      new System() {
        @Override
        public void execute() {
          if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) {
            Game.hero().orElseThrow()
              .fetchOrThrow(InventoryComponent.class)
              .add(new ItemPotionHealth(HealthPotionType.GREATER));
          } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) {
            if (BurningFireballSkill.DAMAGE_AMOUNT == 2) {
              BurningFireballSkill.DAMAGE_AMOUNT = 6;
            } else {
              BurningFireballSkill.DAMAGE_AMOUNT = 2;
            }
            HeroFactory.setHeroSkillCallback(
              new BurningFireballSkill(
                SkillTools::cursorPositionAsPoint)); // Update the current hero skill
            DialogUtils.showTextPopup(
              "Fireball damage set to " + BurningFireballSkill.DAMAGE_AMOUNT,
              "Cheat: Fireball Damage");
          } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3)) {
            Debugger.TELEPORT_TO_END();
          } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_5)) {
            Debugger.TELEPORT_TO_CURSOR();
          } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_6)) {
            BurningFireballSkill.UNLOCKED = !BurningFireballSkill.UNLOCKED;
          }
        }
      });
  }

  /**
   * Extracted from HeroFactory
   * @param hero the hero
   */
  private static void onPressedCloseUI(Entity hero){
    LOGGER.info("Pressed close UI");
    var firstUI = findTopmostUI();
    if (firstUI != null) {
      InventoryGUI.inHeroInventory = false;
      closeUIOnEntity(firstUI);
    }
  }
  private static Tuple<Entity, UIComponent> findTopmostUI(){
    return Game.entityStream() // would be nice to directly access HudSystems
      // stream (no access to the System object)
      .filter(x -> x.isPresent(UIComponent.class))
      // find all Entities which have a UIComponent
      .map(x -> new Tuple<>(x, x.fetchOrThrow(UIComponent.class)))
      // create a tuple to still have access to the UI Entity
      .filter(x -> x.b().closeOnUICloseKey())
      .max(Comparator.comparingInt(x -> x.b().dialog().getZIndex()))
      // find dialog with highest z-Index
      .orElse(null);
  }
  private static void closeUIOnEntity(Tuple<Entity, UIComponent> firstUI){
    if(firstUI == null) return;
    firstUI.a().remove(UIComponent.class);
    if (firstUI.a().componentStream().findAny().isEmpty()) {
      Game.remove(firstUI.a()); // delete unused Entity
    }
  }

  /**
   * Extracted from HeroFactory
   * @param hero the hero
   */
  private static void onPressedInteract(Entity hero){
    LOGGER.info("Pressed interact button");
    UIComponent uiComponent = hero.fetch(UIComponent.class).orElse(null);
    if (uiComponent != null && uiComponent.dialog() instanceof GUICombination && !InventoryGUI.inHeroInventory) {
      // if chest or cauldron
      hero.remove(UIComponent.class);
    } else {
      var firstUI = findTopmostUI();
      if(firstUI == null){
        //No UI open, interact with world as normal
        InteractionTool.interactWithClosestInteractable(hero);
      } else {
        //Already has UI open, close it instead.
        closeUIOnEntity(firstUI);
      }
    }
  }
}
