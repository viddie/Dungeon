package core.components.states;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import core.components.DrawComponent;
import core.utils.JarHelper;
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

  private AnimationConfig config;
  private int frameCount;
  private Sprite[] sprites;

  public Animation(AnimationConfig config){
    this.config = config;
    load();
  }

  private void load(){
    System.out.println("\n");
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

//    outputFileHandle(Gdx.files.getFileHandle(config.path().pathString(), Files.FileType.Internal), Files.FileType.Internal);

    String pathString = config.path().pathString();
    FileHandle fh = Gdx.files.internal(pathString);

    System.out.println("Path string: "+pathString+" | Name: "+fh.name()+" | Name without extension: "+fh.nameWithoutExtension());

    List<IPath> paths = new ArrayList<>();
    if(fh.path().endsWith("/") || fh.name().equals(fh.nameWithoutExtension())){
      System.out.println("FileHandle path: "+fh.path()+" is a directory");

      //2.2. Directory
      //Doesnt work on Desktop
//      for(FileHandle entry : fh.list()){
//        paths.add(new SimpleIPath(entry.path()));
//      }

      //FileHandle.readString() on an internal directory produces a string containing a newline separated list of all
      //files in the folder for some reason????? Not sure if this works on other platforms, but in that case we can just
      //check fh.isDirectory() which will be true
      String[] pathStrings = fh.readString().split("\n");
      Arrays.asList(pathStrings).forEach(s -> paths.add(new SimpleIPath(config.path().pathString()+"/"+s)));

    } else {
//      System.out.println("FileHandle path: "+fh.path()+" is not a directory");
      if(config.config() == null){
        //2.1.
        paths.add(config.path());
      } else {
        //2.3.
        paths.add(config.path());
        return;
      }
    }

    paths.forEach(System.out::println);

    sprites = new Sprite[paths.size()];
    for(int i = 0; i < paths.size(); i++){
      sprites[i] = new Sprite(TextureMap.instance().textureAt(paths.get(i)));
    }


//    File jar = JarHelper.jarFile();
//    try {
//      if (jar == null) loadFromIDE();
//      else loadFromJar(jar);
//    } catch (Exception ex){
//      throw new IllegalArgumentException("Couldn't load animations from config: "+ex.getMessage());
//    }
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
    String path = config.path().pathString();
    if(path.endsWith("/")){
      //2.2. Directory
    } else {
      if(config.config() == null){
        //2.1.
      } else {
        //2.3.
        Texture t = TextureMap.instance().textureAt(config.path());
        //Parse SpritesheetConfig definition for the single image
        sprites = new Sprite[] { new Sprite(t) };
        return;
      }
    }
    throw new UnsupportedOperationException("Not yet implemented");
  }
  private void loadFromIDE() throws URISyntaxException {
    IPath path = config.path();
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
        Texture t = TextureMap.instance().textureAt(config.path());
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
    if(config.isLooping){
      spriteIndex = spriteIndex % sprites.length;
    } else {
      spriteIndex = Math.min(spriteIndex, sprites.length - 1);
    }
    return sprites[spriteIndex];
  }

  public boolean isLooping(){
    return config.isLooping;
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
