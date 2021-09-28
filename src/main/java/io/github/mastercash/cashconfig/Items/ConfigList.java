package io.github.mastercash.cashconfig.Items;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.logging.log4j.Level;

import io.github.mastercash.cashconfig.Constants;

public final class ConfigList extends BaseConfigItem<List<BaseConfigItem<?>>> {

  private Type subType;
  public ConfigList() {
    this("", null, null);
  }
  public ConfigList(String key, List<BaseConfigItem<?>> items, Type subType) {
    super(key,Type.ARRAY);
    if(items != null) {
      value = items;
    }
    else {
      value = new ArrayList<>();
    }
    this.subType = subType;
    for(var item : value) {
      if(this.subType == null) {
        this.subType = item.type;
      }
      else if(!this.subType.equals(item.getType())) {
        throw new InvalidParameterException("Invalid type: " + item.getType() + " is not " + subType);
      }
    }
  }

  public int size() {
    return value.size();
  }

  public Type getSubType() {
    return subType;
  }

  public void AddItem(BaseConfigItem<?> item) {
    if(subType == null) {
      subType = item.getType();
    }
    else if(!subType.equals(item.getType())) {
      throw new InvalidParameterException("Invalid type: " + item.getType() + " is not " + subType);
    }
    value.add(item);
  }

  @Override
  public void toJson(JsonObject parent) {
    var arr = new JsonArray();
    for(var item : value) {
      item.toJson(arr);
    }
    parent.add(this.getKey(), arr);
  }

  @Override
  public void toJson(JsonArray parent) {
    var arr = new JsonArray();
    for(var item: value) {
      item.toJson(arr);
    }
    parent.add(arr);
  }

  @Override
  public void fromJson(JsonElement element) {
    var list = new ArrayList<BaseConfigItem<?>>();
    var arr = element.getAsJsonArray();
    boolean notInitialized = subType == null;
    for(var value : arr) {
      if(notInitialized) {
        var itemType = getType(value);
        if(subType != null && !subType.equals(itemType)) {
          Constants.LOGGER.log(Level.ERROR, "Array element types don't match: " + subType + " doesn't match " + itemType);
          continue;
        }
        else if(subType == null) {
          subType = itemType;
        }
        var item = getInstance(itemType);
        if(item == null) continue;
        item.fromJson(value);
        list.add(item);
      } else {
        if(BaseConfigItem.validType(value, this.subType)) {
          var item = getInstance(this.subType);
          if(item == null) continue;
          item.fromJson(value);
          list.add(item);
        }
      }
    }
    value = list;
  }
}
