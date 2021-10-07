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

package dev.cashire.cashconfig;

import static com.google.common.collect.ImmutableList.of;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.cashire.cashconfig.items.BaseConfigItem;
import dev.cashire.cashconfig.items.BaseConfigItem.Type;
import dev.cashire.cashconfig.items.ConfigGroup;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;


/**
 * Configuration Object. Use this to load and save configuration data from a file.
 */
public final class Config {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private final ConfigGroup items;
  private final File file;

  /**
   * Create new Configuration Instance.
   *
   * @param item item to default in if no values
   * @param file file to read/save to.
   */
  public Config(BaseConfigItem<?> item, @NotNull File file) {
    this(of(item), file);
  }

  /**
   * Create new Configuration Instance.
   *
   * @param items List of items to default in if no values
   * @param file  file to read/save to.
   */ 
  public Config(List<BaseConfigItem<?>> items, @NotNull File file) {
    Objects.requireNonNull(file);
    this.items = new ConfigGroup("root", items);
    this.file = file;
  }

  /**
   * Get list of Configuration items in this configuration.
   * If {@link #readFile()} hasn't been called, this will contain the defaults given.
   *
   * @see #getItem(String, Type) for retrieving an individual item
   * @return list of {@link BaseConfigItem}'s containing configuration data.
   */
  public List<BaseConfigItem<?>> getItems() {
    return items.getValue();
  }

  /**
   * Saves current configuration to a file.
   */
  public void saveFile() {
    JsonObject object = new JsonObject();
    items.toJson(object);

    try (var stream = new FileOutputStream(file)) {
      stream.write(GSON.toJson(object.get("root")).getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Checks to see if the file exists.
   *
   * @return true if file exists, false otherwise
   */
  public boolean hasFile() {
    try (FileInputStream stream = new FileInputStream(file)) {
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Reads configuration from file.
   * Values can be retrieved via {@link #getItem(String, Type)} or {@link #getItems()}
   */
  public void readFile() {
    try (FileInputStream stream = new FileInputStream(file)) {
      byte[] bytes = new byte[stream.available()];
      stream.read(bytes);
      String file = new String(bytes);
      JsonObject parsed = new JsonParser().parse(file).getAsJsonObject();
      items.fromJson(parsed);
    } catch (FileNotFoundException e) {
      saveFile();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Retrieves an Item from the configuration structure.
   *
   * @param path path to item in format: group.item
   * @param type Type of the value expected to find at the end of the path.
   * @return The Configuration item found at the end of the path. If item is not found, null is returned.
   * @throws IllegalArgumentException thrown if {@link BaseConfigItem.Type} doesn't match object's type
   */
  public BaseConfigItem<?> getItem(@NotNull String path, @NotNull Type type) throws IllegalArgumentException {
    Objects.requireNonNull(path);
    Objects.requireNonNull(type);
    var paths = new LinkedList<>(Arrays.asList(path.split("\\.")));
    try {
      var parent = getParent(items, paths);
      var selectedItem = parent.getItem(paths.getLast());
      if (!selectedItem.getType().equals(type)) {
        throw new IllegalArgumentException("Incorrect type " + type + " for " + path + ". Correct type: " + selectedItem.getType());
      }
      return selectedItem;
    } catch (NoSuchElementException e) {
      Constants.LOGGER.log(Level.ERROR, "Item " + paths.getFirst() + " in path " + path + " was not found");
      return null;
    }

  }

  /**
   * Removes an item from the Config File.
   *
   * @param path path to item in format: group.item
   */
  public void removeItem(@NotNull String path) {
    Objects.requireNonNull(path);
    var paths = new LinkedList<>(Arrays.asList(path.split("\\.")));
    try {
      var parent = getParent(items, paths);
      parent.removeItem(paths.getLast());
    } catch (NoSuchElementException e) {
      Constants.LOGGER.log(Level.ERROR, "Item " + paths.getFirst() + " in path " + path + " was not found");
    }
  }

  /**
   * Checks if Config has item at given path.
   *
   * @param path path to item in format: group.item
   * @return true if item found, false otherwise
   */
  public boolean hasItem(@NotNull String path) {
    Objects.requireNonNull(path);
    var paths = new LinkedList<>(Arrays.asList(path.split("\\.")));
    try {
      getParent(items, paths);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  /**
   * Get the type of the item at the given path.
   *
   * @param path path to item in the format: group.item
   * @return Item Type if found, null otherwise.
   */
  public Type getType(@NotNull String path) {
    Objects.requireNonNull(path);
    var paths = new LinkedList<>(Arrays.asList(path.split("\\.")));
    try {
      var parent = getParent(items, paths);
      return parent.getItem(paths.getLast()).getType(); 
    } catch (NoSuchElementException e) {
      return null;
    }
  }

  /**
   * gets the parent node from a given path.
   *
   * @param parent parent node
   * @param paths list of path nodes
   * @return the parent ConfigGroup
   * @throws NoSuchElementException item not found
   */
  private static ConfigGroup getParent(@NotNull ConfigGroup parent, LinkedList<String> paths) throws NoSuchElementException {
    if (paths.size() == 1) {
      if (!parent.hasItem(paths.getFirst())) {
        throw new NoSuchElementException();
      }
      return parent;
    }
    if (paths.size() > 1) {
      var key = paths.getFirst();
      if (!parent.hasItem(key)) {
        throw new NoSuchElementException();
      }
      var item = parent.getItem(key);
      if (!item.isGroup()) {
        throw new NoSuchElementException();
      }
      paths.removeFirst();
      return getParent(parent.getItem(key).asGroup(), paths);
    }
    throw new NoSuchElementException();
  }

}
