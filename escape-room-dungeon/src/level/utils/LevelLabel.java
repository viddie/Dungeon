package level.utils;

/**
 * Holds all available Levels
 */
public enum LevelLabel {
  MainMenu("main_menu", false, "Main Menu"), //End tile: Exit game
  Settings("settings", false, "Settings"), //End tile: Back to main menu
  Tutorial("tutorial", false, "Tutorial"), //End tile: Back to main menu
  Floor1("floor1", true, "Floor 1"), //To next floor
  Floor2("floor2", true, "Floor 2"), //To next floor
  Floor3("floor3", true, "Floor 3"), //To next floor
  GameCompleted("game_completed", false, "Game Completed"), //To next floor
  ;

  public final String fileName;
  public final boolean isActualLevel;
  public final String displayName;
  LevelLabel(String fileName, boolean isActualLevel, String displayName){
    this.fileName = fileName;
    this.isActualLevel = isActualLevel;
    this.displayName = displayName;
  }

  public LevelLabel next(){
    return switch (this){
      case Settings, Tutorial, GameCompleted -> MainMenu;
      case Floor1 -> Floor2;
      case Floor2 -> Floor3;
      case Floor3 -> GameCompleted;
      default -> throw new MissingLevelException("No next level");
    };
  }
}
