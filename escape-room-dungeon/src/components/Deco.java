package components;

import core.components.states.AnimationConfig;
import core.components.states.SpritesheetConfig;
import core.utils.components.path.IPath;
import core.utils.components.path.SimpleIPath;

public enum Deco {
  Tileset("spritesheets/TilesetProps.png", new AnimationConfig().config(new SpritesheetConfig().spriteWidth(176).spriteHeight(208))),
  Tileset2("spritesheets/FD_Dungeon_Free.png", new AnimationConfig().config(new SpritesheetConfig().spriteWidth(512).spriteHeight(384))),
  SignSmall("spritesheets/TilesetProps.png", new AnimationConfig().config(new SpritesheetConfig(0, 12*16, 1, 1))),
  SignBig("spritesheets/TilesetProps.png", new AnimationConfig().config(new SpritesheetConfig(5*16, 12*16, 1, 1).spriteWidth(32))),
  BookshelfLarge("spritesheets/FD_Dungeon_Free.png", new AnimationConfig().config(new SpritesheetConfig(32, 32, 0, 16*16, 1, 1))),
  ;

  private IPath path;
  private AnimationConfig config;
  Deco(String path, AnimationConfig config){
    this.path = new SimpleIPath(path);
    this.config = config;
  }

  public IPath path(){
    return path;
  }
  public AnimationConfig config(){
    return config;
  }
}
