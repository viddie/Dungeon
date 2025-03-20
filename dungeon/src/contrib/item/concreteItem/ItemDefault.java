package contrib.item.concreteItem;

import contrib.item.Item;
import core.utils.components.draw.Animation;
import core.utils.components.path.IPath;

/**
 * A default implementation of {@link Item}.
 *
 * <p>This class is used to create items that do not have any special functionality.
 */
public final class ItemDefault extends Item {

  /**
   * Create a new Item.
   *
   * @param displayName The display name of the item.
   * @param description The description of the item.
   * @param inventoryAnimation The inventory animation of the item.
   * @param worldAnimation The world animation of the item.
   */
  public ItemDefault(
      final String displayName,
      final String description,
      final IPath inventoryAnimation,
      final IPath worldAnimation) {
    super(displayName, description, inventoryAnimation, worldAnimation);
  }
}
