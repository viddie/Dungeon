package utils;

import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectionUtils {

  public static <T> String listToString(List<T> list){
    return list.stream().map(Object::toString).collect(Collectors.joining(", "));
  }

  public static String mapToString(Map<String, Object> map) {
    return map.entrySet().stream()
      .map(entry -> {
        String className = entry.getKey();
        String valueStr = entry.getValue().toString();
        return className + " => " + valueStr;
      })
      .collect(Collectors.joining("\n"));
  }
}
