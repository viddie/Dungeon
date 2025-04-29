package entities;

import core.components.states.AnimationConfig;
import core.components.states.SpritesheetConfig;
import core.utils.Point;
import core.utils.components.draw.DepthLayer;
import core.utils.components.path.IPath;
import core.utils.components.path.SimpleIPath;

public enum Deco {
  Tileset("spritesheets/TilesetProps.png", new SpritesheetConfig().spriteWidth(176).spriteHeight(208)),
  Tileset2("spritesheets/FD_Dungeon_Free.png", new SpritesheetConfig().spriteWidth(512).spriteHeight(384)),
  SignSmall("spritesheets/TilesetProps.png", new SpritesheetConfig(0, 12*16)),
  SignBig("spritesheets/TilesetProps.png", new SpritesheetConfig(5*16, 12*16).spriteWidth(32), new Point(2, 0.5f)),
  BookshelfLarge("spritesheets/FD_Dungeon_Free.png", new SpritesheetConfig(32, 32, 0, 16*16, 1, 1), new Point(2, 1)),
  VaseFull("spritesheets/TilesetProps.png", new SpritesheetConfig(2*16, 7*16), new Point(0.5f, 0.75f), new Point(0.25f, 0)),
  VaseEmpty("spritesheets/TilesetProps.png", new SpritesheetConfig(3*16, 7*16), new Point(0.5f, 0.75f), new Point(0.25f, 0)),
  Bush("spritesheets/TilesetNatures.png", new SpritesheetConfig(2*16, 0*16), new Point(1, 1)),
  Logs("spritesheets/TilesetNatures.png", new SpritesheetConfig(1*16, 0*16), new Point(1, 1)),
  TreeStump("spritesheets/TilesetNatures.png", new SpritesheetConfig(0*16, 0*16), new Point(1, 1)),
  TreeTrunk("spritesheets/TilesetNatures.png", new SpritesheetConfig(7*16, 3*16).spriteWidth(32), new Point(2, 0.8f)),
  StonePillar1("spritesheets/TilesetNatures.png", new SpritesheetConfig(4*16, 1*16).spriteHeight(32), new Point(1, 1)),
  StonePillar2("spritesheets/TilesetNatures.png", new SpritesheetConfig(5*16, 1*16).spriteHeight(32), new Point(1, 1)),
  StonePillar3("spritesheets/TilesetNatures.png", new SpritesheetConfig(6*16, 1*16).spriteHeight(32), new Point(1, 1)),

  ArchL("spritesheets/TilesetNatures.png", new SpritesheetConfig(2*16, 3*16).spriteHeight(32), new Point(0.75f, 2), new Point(0.25f, 0)),
  ArchC("spritesheets/TilesetNatures.png", new SpritesheetConfig(3*16, 3*16).spriteHeight(16), DepthLayer.AbovePlayer.depth()),
  ArchR("spritesheets/TilesetNatures.png", new SpritesheetConfig(4*16, 3*16).spriteHeight(32), new Point(0.75f, 2)),
  ;

  private IPath path;
  private AnimationConfig config;
  private Point defaultCollider = null;
  private Point defaultColliderOffset = null;
  private int defaultDepth;

  Deco(String path, AnimationConfig config){
    this.path = new SimpleIPath(path);
    this.config = config;
  }
  Deco(String path, SpritesheetConfig config){
    this(path, config, null);
  }
  Deco(String path, SpritesheetConfig config, int defaultDepth){
    this(path, config, null, null, defaultDepth);
  }

  /**
   * Creates a new Deco entry. Since a collider is provided, this assumes the collider is placed on the players layer.
   */
  Deco(String path, SpritesheetConfig config, Point defaultCollider){
    this(path, config, defaultCollider, null, DepthLayer.Player.depth());
  }
  Deco(String path, SpritesheetConfig config, Point defaultCollider, int defaultDepth){
    this(path, config, defaultCollider, null, defaultDepth);
  }

  /**
   * Creates a new Deco entry. Since a collider is provided, this assumes the collider is placed on the players layer.
   */
  Deco(String path, SpritesheetConfig config, Point defaultCollider, Point defaultColliderOffset){
    this(path, config, defaultCollider, defaultColliderOffset, DepthLayer.Player.depth());
  }
  Deco(String path, SpritesheetConfig config, Point defaultCollider, Point defaultColliderOffset, int defaultDepth){
    this.path = new SimpleIPath(path);
    this.config = new AnimationConfig().config(config);
    this.defaultCollider = defaultCollider;
    this.defaultColliderOffset = defaultColliderOffset;
    this.defaultDepth = defaultDepth;
  }

  public IPath path(){
    return path;
  }
  public AnimationConfig config(){
    return config;
  }
  public Point defaultCollider(){
    return defaultCollider;
  }
  public Point defaultColliderOffset(){
    return defaultColliderOffset;
  }
  public int defaultDepth() {
    return defaultDepth;
  }
}
