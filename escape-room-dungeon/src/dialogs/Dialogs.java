package dialogs;

import contrib.components.UIComponent;
import core.Entity;
import core.Game;
import modules.showimage.ShowImageUI;

import java.util.function.Consumer;

public class Dialogs {

  public static void openInputDialog(String title, String message, Consumer<String> onClose){
    Entity entity = new Entity("dialog");
    InputDialog dialog = new InputDialog(entity, title, message);
    UIComponent uic = new UIComponent(dialog, true, true, dialog::isInputFocused);
    uic.onClose(() -> {
      onClose.accept(dialog.getInput());
    });
    entity.add(uic);
    Game.add(entity);
  }

}
