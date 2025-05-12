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

  BookshelfLarge("spritesheets/FD_Dungeon_Free.png", new SpritesheetConfig(0, 16*16, 1, 1, 32, 32), new Point(2, 1)),
  Chains0("spritesheets/FD_Dungeon_Free.png", new SpritesheetConfig(17*16, 5*16)),
  Chains1("spritesheets/FD_Dungeon_Free.png", new SpritesheetConfig(18*16, 5*16)),
  Chains2("spritesheets/FD_Dungeon_Free.png", new SpritesheetConfig(19*16, 5*16)),
  Chains3("spritesheets/FD_Dungeon_Free.png", new SpritesheetConfig(20*16, 5*16)),
  Chains4("spritesheets/FD_Dungeon_Free.png", new SpritesheetConfig(16*16, 6*16)),
  Chains5("spritesheets/FD_Dungeon_Free.png", new SpritesheetConfig(17*16, 6*16)),
  Chains6("spritesheets/FD_Dungeon_Free.png", new SpritesheetConfig(18*16, 6*16)),
  Chains7("spritesheets/FD_Dungeon_Free.png", new SpritesheetConfig(19*16, 6*16)),
  Chains8("spritesheets/FD_Dungeon_Free.png", new SpritesheetConfig(20*16, 6*16)),

  SignSmall("spritesheets/TilesetProps.png", new SpritesheetConfig(0, 12*16)),
  SignBig("spritesheets/TilesetProps.png", new SpritesheetConfig(5*16, 12*16).spriteWidth(32), new Point(2, 0.5f)),
  VaseFull("spritesheets/TilesetProps.png", new SpritesheetConfig(2*16, 7*16), new Point(0.5f, 0.75f), new Point(0.25f, 0)),
  VaseEmpty("spritesheets/TilesetProps.png", new SpritesheetConfig(3*16, 7*16), new Point(0.5f, 0.75f), new Point(0.25f, 0)),


  //Player
  Bush("spritesheets/TilesetNatures.png", new SpritesheetConfig(2*16, 0*16), new Point(1, 1)),
  Logs("spritesheets/TilesetNatures.png", new SpritesheetConfig(1*16, 0*16), new Point(1, 1)),
  TreeStump("spritesheets/TilesetNatures.png", new SpritesheetConfig(0*16, 0*16), new Point(1, 1)),
  TreeTrunk("spritesheets/TilesetNatures.png", new SpritesheetConfig(7*16, 3*16).spriteWidth(32), new Point(2, 0.8f)),
  StonePillar0("spritesheets/TilesetNatures.png", new SpritesheetConfig(4*16, 1*16).spriteHeight(32), new Point(1, 1)),
  StonePillar1("spritesheets/TilesetNatures.png", new SpritesheetConfig(5*16, 1*16).spriteHeight(32), new Point(1, 1)),
  StonePillar2("spritesheets/TilesetNatures.png", new SpritesheetConfig(6*16, 1*16).spriteHeight(32), new Point(1, 1)),
  BigBush("spritesheets/TilesetNatures.png", new SpritesheetConfig(7*16, 0*16, 1, 1, 48, 48), new Point(2.5f, 1.5f), new Point(0.25f, 0.5f)),
  StoneAltar("spritesheets/TilesetNatures.png", new SpritesheetConfig(0*16, 2*16).spriteWidth(32), new Point(2, 0.75f)),

  //Background
  Rubble0("spritesheets/TilesetNatures.png", new SpritesheetConfig(0*16, 1*16)),
  Rubble1("spritesheets/TilesetNatures.png", new SpritesheetConfig(1*16, 1*16)),
  Rubble2("spritesheets/TilesetNatures.png", new SpritesheetConfig(2*16, 1*16)),
  Rubble3("spritesheets/TilesetNatures.png", new SpritesheetConfig(3*16, 1*16)),
  Campfire("spritesheets/TilesetNatures.png", new SpritesheetConfig(6*16, 0*16)),
  Mushrooms0("spritesheets/TilesetNatures.png", new SpritesheetConfig(4*16, 0*16)),
  Mushrooms1("spritesheets/TilesetNatures.png", new SpritesheetConfig(5*16, 0*16)),


  ArchL("spritesheets/TilesetNatures.png", new SpritesheetConfig(2*16, 3*16).spriteHeight(32), new Point(0.75f, 1.0f), new Point(0.25f, 0)),
  ArchC("spritesheets/TilesetNatures.png", new SpritesheetConfig(3*16, 3*16).spriteHeight(16), DepthLayer.AbovePlayer.depth()),
  ArchR("spritesheets/TilesetNatures.png", new SpritesheetConfig(4*16, 3*16).spriteHeight(32), new Point(0.75f, 1.0f)),
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
    this(path, config, defaultCollider, null, defaultCollider == null ? DepthLayer.BackgroundDeco.depth() : DepthLayer.Player.depth());
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
