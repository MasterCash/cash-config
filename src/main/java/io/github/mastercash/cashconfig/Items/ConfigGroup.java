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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class ConfigGroup extends BaseConfigItem<List<BaseConfigItem<?>>> {
  protected Map<String, BaseConfigItem<?>> _items;

  public ConfigGroup() {
    this("", null);
  }
  public ConfigGroup(String key, List<BaseConfigItem<?>> items) {
    super(key, Type.GROUP);
    _items = new HashMap<>();
    value = items != null ? items : new ArrayList<>();

    for(var item : value) {
      if(_items.containsKey(item.getKey())) {
        throw new InvalidParameterException("Duplicate key: " + item.getKey());
      }
      _items.put(item.getKey(), item);
    }
  }

  public int size() {
    return value.size();
  }

  public boolean AddItem(BaseConfigItem<?> item) {
    if(_items.containsKey(item.getKey())) return false;
    _items.put(item.getKey(), item);
    this.value.add(item);
    return true;
  }

  public BaseConfigItem<?> GetItem(String key) {
    return _items.get(key);
  }

  public boolean HasItem(String key) {
    return _items.containsKey(key);
  }

  @Override
  public void toJson(JsonObject parent) {
    var element = new JsonObject();
    for(var item : value) {
      item.toJson(element);
    }
    parent.add(key, element);
  }

  @Override
  public void toJson(JsonArray parent) {
    var element = new JsonObject();
    for(var item : value) {
      item.toJson(element);
    }
    parent.add(element);
  }

  @Override
  public void fromJson(JsonElement element) {
    var obj = element.getAsJsonObject();
    var empty = _items.isEmpty();
    for(var entry : obj.entrySet()) {
      if(empty) {
        if(entry.getValue().isJsonArray()) {
          var item = new ConfigList(entry.getKey(), null, null);
          item.fromJson(entry.getValue());
          _items.put(entry.getKey(), item);
          value.add(item);
        }
        else if(entry.getValue().isJsonObject()) {
          var item = new ConfigGroup(entry.getKey(), null);
          item.fromJson(entry.getValue());
          _items.put(entry.getKey(), item);
          value.add(item);
        }
        else if(entry.getValue().isJsonPrimitive()) {
          var prim = entry.getValue().getAsJsonPrimitive();
          if(prim.isString()) {
            var item = new ConfigString(entry.getKey(), null);
            item.fromJson(prim);
            _items.put(entry.getKey(), item);
            value.add(item);
          }
          else if(prim.isNumber()) {
            var item = new ConfigNumber(entry.getKey(), null);
            item.fromJson(prim);
            _items.put(entry.getKey(), item);
            value.add(item);
          }
          else if(prim.isBoolean()) {
            var item = new ConfigBoolean(entry.getKey(), null);
            item.fromJson(prim);
            _items.put(entry.getKey(), item);
            value.add(item);
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

  @Override
  public List<BaseConfigItem<?>> getValue() {
    return Collections.unmodifiableList(value);
  }

}
