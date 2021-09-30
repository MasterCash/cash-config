/*
 * MIT License
 * 
 * Copyright (c) 2021 Josh Cash
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.mastercash.cashconfig.Items;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import io.github.mastercash.cashconfig.Constants;

/**
 * Configuration item for a json array for {@link BaseConfigItem}
 */
public final class ConfigList extends BaseConfigItem<List<BaseConfigItem<?>>> {

  private Type subType;

  /**
   * Creates an empty Array item with no key.
   * Should not be used if adding this item to {@link ConfigGroup}
   * The first item added (via {@link #AddItem(BaseConfigItem)}) will set {@link Type} of the items stored in the array.
   */
  public ConfigList() {
    this("", null, null);
  }

  /**
   * Creates an Array item with given key and items
   * null is a valid parameter for items and subType.
   * If items is null at creation, a empty list will be used instead.
   * If subType is null at creation, the first item added (whether by items parameter or {@link #AddItem(BaseConfigItem)}) will set {@link Type} of the items stored in the array.
   * @param key The key to be used if put in a group.
   * @param items List of items to add to this array by default.
   * @param subType The type of this array
   * @throws InvalidParameterException if an item in the array doesn't match subType
   */
  public ConfigList(@NotNull String key, List<BaseConfigItem<?>> items, Type subType) {
    super(Objects.requireNonNull(key),Type.ARRAY);
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

  /**
   * The amount of items in this Array Item
   * @return count of {@link #getValue()}
   */
  public int size() {
    return value.size();
  }

  @Override
  public void setValue(@NotNull List<BaseConfigItem<?>> value) {
    Objects.requireNonNull(value);
    if(value.size() == 0) return;
    for(var item : value) {
      if(!item.type.equals(type)) throw new IllegalArgumentException("type " + item.type.toString() + " from item in value doesn't match list type " + type.toString());
    }
    this.value = value;
  }

  /**
   * Gets the type that the this array item is limited to.
   * if this value is null, the first item added with {@link #AddItem(BaseConfigItem)} will set this type.
   * @return {@link Type} of the items.
   */
  public Type getSubType() {
    return subType;
  }

  /**
   * Adds an item to this Array Item.
   * if subType is null, then this item will set the type for this array based on it's type.
   * @param item item to add.
   * @throws InvalidParameterException type of item did not match {@link #getSubType()}
   */
  public void AddItem(@NotNull BaseConfigItem<?> item) {
    Objects.requireNonNull(item);
    if(subType == null) {
      subType = item.getType();
    }
    else if(!subType.equals(item.getType())) {
      throw new InvalidParameterException("Invalid type: " + item.getType() + " is not " + subType);
    }
    value.add(item);
  }

  /**
   * Returns the item at the given index
   * @param index position in the list 
   * @return item that was found
   */
  public BaseConfigItem<?> GetItem(int index) {
    Objects.checkIndex(index, value.size());
    return value.get(index);
  }

  /**
   * Remove an Item from the list
   * @param index position to remove
   * @return item if removed, null otherwise
   */
  public BaseConfigItem<?> RemoveValue(int index) {
    Objects.checkIndex(index, value.size());
    return value.remove(index);
  }

  @Override
  public void toJson(@NotNull JsonObject parent) {
    Objects.requireNonNull(parent);
    var arr = new JsonArray();
    for(var item : value) {
      item.toJson(arr);
    }
    parent.add(this.getKey(), arr);
  }

  @Override
  public void toJson(@NotNull JsonArray parent) {
    Objects.requireNonNull(parent);
    var arr = new JsonArray();
    for(var item: value) {
      item.toJson(arr);
    }
    parent.add(arr);
  }

  @Override
  public void fromJson(@NotNull JsonElement element) {
    Objects.requireNonNull(element);
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

  /**
   * the return value is an unmodifiable view of the list {@link Collections#unmodifiableList(List)}
   */
  @Override
  public List<BaseConfigItem<?>> getValue() {
    return Collections.unmodifiableList(value);
  }
}
