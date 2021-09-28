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
package io.github.mastercash.cashconfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.logging.log4j.Level;

import io.github.mastercash.cashconfig.Items.BaseConfigItem;
import io.github.mastercash.cashconfig.Items.ConfigGroup;
import io.github.mastercash.cashconfig.Items.BaseConfigItem.Type;

public final class Config {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private final Map<String, BaseConfigItem<?>> items;
  private final File file;
  
  public Config(List<BaseConfigItem<?>> items, File file) {
    this.items = new HashMap<>();
    if(items != null) {
      for(var item : items) {
        this.items.put(item.getKey(), item);
      }
    }
    if(file == null) throw new InvalidParameterException("file is null");
    this.file = file;
  }

  public List<BaseConfigItem<?>> getItems() {
    return new ArrayList<>(items.values());
  }

  public void saveFile() {
    JsonObject object = new JsonObject();
    for(var item : items.values()) {
      item.toJson(object);
    }

    try (var stream = new FileOutputStream(file)) {
      stream.write(GSON.toJson(object).getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean hasFile() {
    try (FileInputStream stream = new FileInputStream(file)) {
      return true;
    }
    catch(Exception e) {
      return false;
    }
  }

  public void readFile() {
    try (FileInputStream stream = new FileInputStream(file)) {
      byte[] bytes = new byte[stream.available()];
      stream.read(bytes);
      String file = new String(bytes);
      JsonObject parsed = new JsonParser().parse(file).getAsJsonObject();
      for(var entry : parsed.entrySet()) {
        if(items.containsKey(entry.getKey())) {
          items.get(entry.getKey()).fromJson(entry.getValue());
        } else {
          var type = BaseConfigItem.getType(entry.getValue());
          if(type != null) {
            var item = BaseConfigItem.getInstance(type, entry.getKey());
            item.fromJson(entry.getValue());
            items.put(item.getKey(), item);
          }
          else {
            Constants.LOGGER.log(Level.ERROR, "No type for JSON key: " + entry.getKey() + " - " + entry.getValue().toString());
          }
        }
      }
    } catch (FileNotFoundException e) {
      saveFile();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public BaseConfigItem<?> getItem(String path, Type type) throws IllegalArgumentException {
    var splitPath = path.split("\\.");
    var selectedItem = items.get(splitPath[0]);
    var paths = new LinkedList<>(Arrays.asList(splitPath));
    try {
      while(selectedItem instanceof ConfigGroup) {
        paths.removeFirst();
        selectedItem = ((ConfigGroup) selectedItem).GetItem(paths.getFirst());
        if(selectedItem == null) throw new NoSuchElementException();
      }
    } catch(NoSuchElementException e) {
      Constants.LOGGER.log(Level.ERROR, "Item " + paths.getFirst() + " in path " + path + " was not found");
      return null;
    }
    if(!selectedItem.getType().equals(type)) {
      throw new IllegalArgumentException("Incorrect type " + type + " for " + path + ". Correct type: " + selectedItem.getType());
    }

    return selectedItem;
  }
}
