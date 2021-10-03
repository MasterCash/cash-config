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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

/**
 * Configuration item for a json object for {@link BaseConfigItem}
 */
public final class ConfigGroup extends BaseConfigItem<List<BaseConfigItem<?>>> {
  private Map<String, BaseConfigItem<?>> _items;

  /**
   * Creates an empty Group item with no key.
   * Should not be used if adding this item to {@link ConfigGroup}
   */
  public ConfigGroup() {
    this("", null);
  }

  /**
   * Creates an empty Group item with given key.
   * @param key The key to be used if put in a group.
   */
  public ConfigGroup(@NotNull String key) {
    this(key, null);
  }

  /**
   * Creates a Group item with given key and items.
   * null is a valid parameter for items
   * If items is null at creation, an empty list will be used instead.
   * @param key The key to be used if put in a group.
   * @param items List of items to add to this group by default.
   * @throws InvalidParameterException Two items in items have the same key.
   */
  public ConfigGroup(@NotNull String key, List<BaseConfigItem<?>> items) {
    super(Objects.requireNonNull(key), Type.GROUP);
    _items = new HashMap<>();
    if(items == null) items = new ArrayList<>();
    for(var item : items) {
      if(_items.containsKey(item.getKey())) {
        throw new InvalidParameterException("Duplicate key: " + item.getKey());
      }
      _items.put(item.getKey(), item);
    }
  }

  /**
   * The amount of items in this Group item.
   * @return count of {@link #getValue()}
   */
  public int size() {
    return _items.size();
  }

  /**
   * Adds an item to this Group item.
   * @param item item to add.
   * @return true if added false if key for that item already exists.
   */
  public boolean AddItem(@NotNull BaseConfigItem<?> item) {
    Objects.requireNonNull(item);
    if(_items.containsKey(item.getKey())) return false;
    _items.put(item.getKey(), item);
    return true;
  }

  /**
   * Gets an item with the given key
   * @param key key to look for.
   * @return the item if found, otherwise null
   */
  public BaseConfigItem<?> GetItem(@NotNull String key) {
    Objects.requireNonNull(key);
    return _items.get(key);
  }

  public BaseConfigItem<?> RemoveItem(@NotNull String key) {
    Objects.requireNonNull(key);
    return _items.remove(key);
  }

  public BaseConfigItem<?> SetItem(@NotNull BaseConfigItem<?> item) {
    Objects.requireNonNull(item);
    return _items.put(item.getKey(), item);
  }

  /**
   * Checks to see if an item of a given key exists
   * @param key key to check
   * @return true if item exits, false otherwise.
   */
  public boolean HasItem(@NotNull String key) {
    Objects.requireNonNull(key);
    return _items.containsKey(key);
  }

  @Override
  public void toJson(@NotNull JsonObject parent) {
    Objects.requireNonNull(parent);
    var element = new JsonObject();
    for(var item : _items.values()) {
      item.toJson(element);
    }
    parent.add(key, element);
  }

  @Override
  public void toJson(@NotNull JsonArray parent) {
    Objects.requireNonNull(parent);
    var element = new JsonObject();
    for(var item : _items.values()) {
      item.toJson(element);
    }
    parent.add(element);
  }

  @Override
  public void fromJson(@NotNull JsonElement element) {
    Objects.requireNonNull(element);
    var obj = element.getAsJsonObject();
    var empty = _items.isEmpty();
    for(var entry : obj.entrySet()) {
      if(empty) {
        if(entry.getValue().isJsonArray()) {
          var item = new ConfigList(entry.getKey(), null, null);
          item.fromJson(entry.getValue());
          _items.put(entry.getKey(), item);
        }
        else if(entry.getValue().isJsonObject()) {
          var item = new ConfigGroup(entry.getKey(), null);
          item.fromJson(entry.getValue());
          _items.put(entry.getKey(), item);
        }
        else if(entry.getValue().isJsonPrimitive()) {
          var prim = entry.getValue().getAsJsonPrimitive();
          if(prim.isString()) {
            var item = new ConfigString(entry.getKey(), null);
            item.fromJson(prim);
            _items.put(entry.getKey(), item);
          }
          else if(prim.isNumber()) {
            var item = new ConfigNumber(entry.getKey(), null);
            item.fromJson(prim);
            _items.put(entry.getKey(), item);
          }
          else if(prim.isBoolean()) {
            var item = new ConfigBoolean(entry.getKey(), null);
            item.fromJson(prim);
            _items.put(entry.getKey(), item);
          }
        }
      } else {
        if(_items.containsKey(entry.getKey())) {
          var item = _items.get(entry.getKey());
          item.fromJson(entry.getValue());
        }
      }
    }
  }

  /**
   * the return value is an unmodifiable view of the list {@link Collections#unmodifiableList(List)}
   */
  @Override
  public List<BaseConfigItem<?>> getValue() {
    return Collections.unmodifiableList(new ArrayList<>(_items.values()));
  }

}
