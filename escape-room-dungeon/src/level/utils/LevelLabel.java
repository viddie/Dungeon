package level.utils;

/**
 * Holds all available Levels
 */
public enum LevelLabel {
  MainMenu("main_menu", false), //End tile: Exit game
  Settings("settings", false), //End tile: Back to main menu
  Tutorial("tutorial", false), //End tile: Back to main menu
  Floor1("floor1", true), //To next floor
  ;

  public final String fileName;
  public final boolean isActualLevel;
  private LevelLabel(String fileName, boolean isActualLevel){
    this.fileName = fileName;
    this.isActualLevel = isActualLevel;
  }

  public LevelLabel next(){
    return switch (this){
      case Settings, Tutorial -> MainMenu;
      default -> throw new MissingLevelException("");
    };
  }
}
