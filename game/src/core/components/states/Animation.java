package core.components.states;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import core.components.DrawComponent;
import core.utils.components.draw.TextureMap;
import core.utils.components.path.IPath;
import core.utils.components.path.SimpleIPath;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class Animation {

  private static final IPath MISSING_TEXTURE_PATH = new SimpleIPath("animation/missing_texture.png");
  private static final float DEFAULT_SCALE = 1f / 16;

  private IPath path;
  private AnimationConfig config;
  private float width = 1;
  private float height = 1;
  private int frameCount;
  private Sprite[] sprites;

  /**
   * A new animation
   * @param path An IPath to either a single image or a folder of images
   * @param config The configuration to use for this animation
   */
  public Animation(IPath path, AnimationConfig config){
    this.path = path;
    if(config == null) this.config = new AnimationConfig();
    else this.config = config;
    load();
  }
  public Animation(IPath path){
    this(path, new AnimationConfig());
  }

  /*
   * Algorithm:
   * 1. Figure out if we are running from a jar
   * 2. Get all Textures specified through the AnimationConfig (cached)
   * 2.1. If single path, load the image as single Texture in a Texture[]
   * 2.2. If the path leads to a directly, load all images into a Texture[]
   * 2.3. If single path and SpritesheetConfig is set, load the image as single Texture and get all relevant regions as TextureRegion[]
   * 3. Load Textures into Sprites (cached)
   * 3.1. If Texture[], load into Sprites (cached)
   * 3.2. If TextureRegion[], load into Sprites (cached (somehow))
   * 4. Set the sprites array
   */
  private void load(){
    String pathString = path.pathString();
    if(pathString.endsWith("/")){
      pathString = pathString.substring(0, pathString.length() - 1);
    }
    FileHandle fh = Gdx.files.internal(pathString);
//    System.out.println("Path string: "+pathString+" | Name: "+fh.name()+" | Name without extension: "+fh.nameWithoutExtension());

    List<IPath> paths = new ArrayList<>();
    if(fh.name().equals(fh.nameWithoutExtension())){
      //2.2. Directory
      //Doesnt work on Desktop
//      for(FileHandle entry : fh.list()){
//        paths.add(new SimpleIPath(entry.path()));
//      }

      //FileHandle.readString() on an internal directory produces a string containing a newline separated list of all
      //files in the folder for some reason????? Not sure if this works on other platforms, but in that case we can just
      //check fh.isDirectory() which will be true
      String[] pathStrings = fh.readString().split("\n");
      Arrays.asList(pathStrings).forEach(s -> paths.add(new SimpleIPath(path.pathString()+"/"+s)));

    } else {
      if(config.config() == null){
        //2.1.
        paths.add(path);
      } else {
        //2.3.
        Texture spritesheet = TextureMap.instance().textureAt(path);
        SpritesheetConfig ssc = config.config();
        int sWidth = ssc.spriteWidth();
        int sHeight = ssc.spriteHeight();
        int offsetX = ssc.x();
        int offsetY = ssc.y();

        sprites = new Sprite[ssc.rows() * ssc.columns()];
        for(int y = 0; y < ssc.rows(); y++){
          for(int x = 0; x < ssc.columns(); x++){
            int index = y * ssc.columns() + x;
            sprites[index] = new Sprite(new TextureRegion(spritesheet, offsetX + sWidth * x, offsetY + sHeight * y, sWidth, sHeight));
          }
        }

        width = sWidth * config.scaleX();
        height = config.scaleY() == 0 ? sHeight * config.scaleX() : sHeight * config.scaleY();
        width *= DEFAULT_SCALE;
        height *= DEFAULT_SCALE;
        return;
      }
    }

//    paths.forEach(System.out::println);

    sprites = new Sprite[paths.size()];
    for(int i = 0; i < paths.size(); i++){
      sprites[i] = new Sprite(TextureMap.instance().textureAt(paths.get(i)));
    }

    //Set the sprite width/height based on the scaling values and the texture width/height
    Texture t = TextureMap.instance().textureAt(paths.get(0));
    width = t.getWidth() * config.scaleX();
    height = config.scaleY() == 0 ? t.getHeight() * config.scaleX() : t.getHeight() * config.scaleY();

    width *= DEFAULT_SCALE;
    height *= DEFAULT_SCALE;
  }

  private void outputFileHandle(FileHandle fh, Files.FileType type){
    System.out.println("FileHandle: "+type.name());
    System.out.println("\tpath: "+fh.path());
    System.out.println("\texists: "+fh.exists());
    System.out.println("\tisDirectory: "+fh.isDirectory());
    System.out.println("\tabsolutePath: "+fh.file().getAbsolutePath());
    System.out.println("\tfile().exists: "+fh.file().exists());
    System.out.println("\tfile().isDirectory: "+fh.file().isDirectory());
    if(fh.exists()){
      System.out.println("\treadString: "+fh.readString());
    }
  }

  private void loadFromJar(File jar){
    String pathStr = path.pathString();
    if(pathStr.endsWith("/")){
      //2.2. Directory
    } else {
      if(config.config() == null){
        //2.1.
      } else {
        //2.3.
        Texture t = TextureMap.instance().textureAt(path);
        //Parse SpritesheetConfig definition for the single image
        sprites = new Sprite[] { new Sprite(t) };
        return;
      }
    }
    throw new UnsupportedOperationException("Not yet implemented");
  }
  private void loadFromIDE() throws URISyntaxException {
    List<IPath> texturePaths;
    if(isDirectoryIDE(path)){
      //2.2. Directory
      URL url = DrawComponent.class.getResource("/" + path.pathString());
      File dir = new File(url.toURI());
      texturePaths = Arrays.stream(Objects.requireNonNull(dir.listFiles()))
        .filter(File::isDirectory)
        .map(File::getName).map(SimpleIPath::new).collect(Collectors.toList());

    } else {
      if(config.config() == null){
        //2.1.
        texturePaths = new ArrayList<>();
        texturePaths.add(path);
      } else {
        //2.3.
        Texture t = TextureMap.instance().textureAt(path);
        //Parse SpritesheetConfig definition for the single image
        sprites = new Sprite[] { new Sprite(t) };
        return;
      }
    }

    sprites = new Sprite[texturePaths.size()];
    for(int i = 0; i < texturePaths.size(); i++){
      sprites[i] = new Sprite(TextureMap.instance().textureAt(texturePaths.get(i)));
    }
  }

  public Sprite getSprite(){
    int spriteIndex = frameCount / config.framesPerSprite();
    if(config.isLooping()){
      spriteIndex = spriteIndex % sprites.length;
    } else {
      spriteIndex = Math.min(spriteIndex, sprites.length - 1);
    }
    return sprites[spriteIndex];
  }
  public float getSpriteWidth(){
    return width;
  }
  public float getSpriteHeight(){
    return height;
  }

  public boolean isLooping(){
    return config.isLooping();
  }

  public boolean isFinished(){
    int spriteIndex = frameCount / config.framesPerSprite();
    return spriteIndex >= sprites.length;
  }

  public void update(){
    frameCount++;
  }
  public int frameCount() {
    return frameCount;
  }
  public void frameCount(int frameCount) {
    this.frameCount = frameCount;
  }
  public AnimationConfig getConfig(){ return config; }

  //  /**
//   * Load the animation assets if the game is running in a JAR.
//   *
//   * <p>This function will create a map of directories ({@link String}) and the files ({@link
//   * LinkedList}) inside these directories. The map will be filled with the directories inside the
//   * given path (e.g., "character/knight"). Ultimately, this function will manually create an
//   * Animation for each entry within this map.
//   *
//   * @param path Path to the assets.
//   * @param jarFile Path to the JAR files.
//   * @throws IOException if the JAR file or the files in the JAR file cannot be read.
//   */
//  private void loadAnimationsFromJar(final IPath path, final File jarFile) throws IOException {
//
//    JarFile jar = new JarFile(jarFile);
//    Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
//
//    // This will be used to map the directory names (e.g., "idle") and the texture files.
//    // Ultimately, we will create animations out of this by using the
//    // Animation(LinkedList<String>) constructor.
//
//    HashMap<String, List<IPath>> storage = new HashMap<>();
//    animationMap = new HashMap<>();
//
//    // Iterate over each file and directory in the JAR.
//
//    while (entries.hasMoreElements()) {
//      // example: character/knight/idle_down/idle_down_knight_1.png
//      // but also: character/knight/idle/
//      // and: character/knight/
//      String fileName = entries.nextElement().getName();
//
//      // If the entry starts with the path name (character/knight/idle),
//      // this is true for entries like (character/knight/idle_down/idle_down_knight_1.png) and
//      // (character/knight/idle/).
//      if (fileName.startsWith(path.pathString() + "/")) {
//
//        // Get the index of the last FileSeparator; every character after that separator is
//        // part of the filename.
//        int lastSlashIndex = fileName.lastIndexOf("/");
//
//        // Ignore directories, so we only work with strings like
//        // (character/knight/idle_down/idle_down_knight_1.png).
//        if (lastSlashIndex != fileName.length() - 1) {
//          // Get the index of the second-to-last part of the string.
//          // For example, in "character/knight/idle_down/idle_down_knight_1.png", this would be the
//          // index of the slash in "/idle".
//
//          int secondLastSlashIndex = fileName.lastIndexOf("/", lastSlashIndex - 1);
//
//          // Get the name of the directory. The directory name is between the
//          // second-to-last and the last separator index.
//          // The directory name serves as the key of the animation in the animation map
//          // (similar to what the IPATh values are for them).
//          // For example: "idle"
//
//          String lastDir = fileName.substring(secondLastSlashIndex + 1, lastSlashIndex);
//
//          // add animation-files to new or existing storage map
//          if (storage.containsKey(lastDir)) storage.get(lastDir).add(new SimpleIPath(fileName));
//          else {
//            LinkedList<IPath> list = new LinkedList<>();
//            list.add(new SimpleIPath(fileName));
//            storage.put(lastDir, list);
//          }
//        }
//      }
//    }
//
//    // sort the files in lexicographic order (like the most os)
//    // animations will be played in order
//    storage.values().forEach(x -> x.sort(Comparator.comparing(IPath::pathString)));
//    // create animations
//    storage.forEach(
//      (name, textureSet) -> animationMap.put(name, core.utils.components.draw.Animation.fromCollection(textureSet)));
//    jar.close();
//  }
//
//  /**
//   * Load animations if the game is running in the IDE (or over the shell).
//   *
//   * @param path Path to the animations.
//   */
//  private void loadAnimationsFromIDE(final IPath path) {
//
//
//    URL url = DrawComponent.class.getResource("/" + path.pathString());
//    if (url != null) {
//      try {
//        try {
//          File apps = new File(url.toURI());
//          animationMap =
//            Arrays.stream(Objects.requireNonNull(apps.listFiles()))
//              .filter(File::isDirectory)
//              .collect(Collectors.toMap(File::getName, DrawComponent::allFilesFromDirectory));
//        } catch (IllegalArgumentException e) {
//          LOGGER.log(
//            CustomLogLevel.ERROR, "Could not load animations from directory: " + url.toURI(), e);
//        }
//      } catch (URISyntaxException e) {
//        LOGGER.log(CustomLogLevel.ERROR, "Could not load animations from directory", e);
//      }
//    }
//  }

  private static boolean isDirectoryIDE(final IPath path){
    URL url = Animation.class.getResource("/" + path.pathString());
    if (url != null) {
      try {
          File apps = new File(url.toURI());
          return apps.isDirectory();
      } catch (URISyntaxException e) {
//        LOGGER.log(CustomLogLevel.ERROR, "Could not load animations from directory", e);
      }
    }
    throw new IllegalArgumentException("Path is not valid");
  }
}
