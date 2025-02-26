package modules.showimage;

import core.Component;
import core.Entity;
import core.utils.IVoidFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ShowImageComponent implements Component {

  public String imagePath;
  public boolean isUIOpen = false;
  public BiConsumer<Entity, Entity> onOpenAction;
  public BiConsumer<Entity, Entity> onCloseAction;
  public Entity overlay;

  public ShowImageComponent(String imagePath){
    this.imagePath = imagePath;
  }

  public void onOpen(Entity entity, Entity overlay){
    if(onOpenAction != null){
      onOpenAction.accept(entity, overlay);
    }
  }

  public void onClose(Entity entity, Entity overlay){
    if(onCloseAction != null){
      onCloseAction.accept(entity, overlay);
    }
  }
}
