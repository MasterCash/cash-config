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

import java.util.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * Base type of all Configuration Items.
 * @param <T> type stored in this item. Supported types listed in {@link BaseConfigItem.Type}
 */
public abstract class BaseConfigItem<T> {
  protected final String key;
  protected T value;
  protected final Type type;
  /**
   * Serializes data into the given {@link JsonObject}.
   * This item is put in the {@link JsonObject} at key value given by {@link #getKey()}.
   * It is put in as type given by {@link #getType()}
   * @param parent {@link JsonObject} to add this item to.
   */
  public abstract void toJson(@NotNull JsonObject parent); 
  /**
   * Serializes data into the given {@link JsonArray}
   * This item's {@link #getKey()} value is ignored in this case.
   * It is put in as type given by {@link #getType()}
   * @param parent
   */
  public abstract void toJson(@NotNull JsonArray parent);
  /**
   * Deserialize data from the {@link JsonElement} into this item.
   * Treats the {@link JsonElement} as the type given by {@link #getType()}
   * @param element
   */
  public abstract void fromJson(@NotNull JsonElement element);

  /**
   * Gets the type of this item.
   * @see BaseConfigItem.Type
   * @return type of this item.
   */
  public Type getType() {
    return type;
  }

  /**
   * The key this value is stored under in an object.
   * If from an array this value may be empty.
   * @return
   */
  public String getKey() {
    return key;
  }

  /**
   * Value stored by this item. Item type corresponds to this items type
   * @see BaseConfigItem.Type
   * @return value stored by this item type.
   */
  public T getValue() {
    return value;
  }

  /**
   * Set the value of this item.
   * @param value value to use instead of current value.
   */
  public void setValue(@NotNull T value) {
    this.value = Objects.requireNonNull(value);
  }


  /**
   * Checks if the given json element matches the given type
   * @param element json element to use
   * @param type type to check with
   * @return true if element matches type, false otherwise
   */
  protected static boolean validType(@NotNull JsonElement element, @NotNull Type type) {
    Objects.requireNonNull(element);
    Objects.requireNonNull(type);
    if(element.isJsonObject()) return type.equals(Type.GROUP);
    if(element.isJsonArray()) return type.equals(Type.ARRAY);
    if(element.isJsonPrimitive()) {
      var prim = element.getAsJsonPrimitive();
      if(prim.isString()) return type.equals(Type.STRING);
      if(prim.isBoolean()) return type.equals(Type.BOOLEAN);
      if(prim.isNumber()) return type.equals(Type.NUMBER);
    }
    return false;
  }

  /**
   * BaseConfigItem constructor.
   * @param key
   * @param type
   */
  protected BaseConfigItem(@NotNull String key, @NotNull Type type) {
    this.key = Objects.requireNonNull(key);
    this.type = Objects.requireNonNull(type);
  }

  /**
   * Checks if this item is a group item
   * @return true if group item, false otherwise
   */
  public boolean isGroup() {
    return type.equals(Type.GROUP);
  }

  /**
   * gets this item as a group item
   * @return this as a group item
   * @throws IllegalStateException if not a group item
   */
  public ConfigGroup asGroup() {
    if(!isGroup()) throw new IllegalStateException("Item is not a Group");
    return (ConfigGroup) this;
  }

  /**
   * Checks if this item is a list item
   * @return true if list item, false otherwise
   */
  public boolean isList() {
    return type.equals(Type.ARRAY);
  }
  
  /**
   * Checks if this item is a list item with given subtype
   * @param subType type to check with
   * @return true if list item with given subtype, false otherwise
   */
  public boolean isList(Type subType) {
    if(isList()) {
      var itemType = ((ConfigList)this).getSubType(); 
      return subType == null || itemType == null ? subType == itemType : subType.equals(itemType);
    }
    return false;
  }
  /**
   * Gets this item as a list item
   * @return this as list item
   * @throws IllegalStateException if not a list item
   */
  public ConfigList asList() {
    if(!isList()) throw new IllegalStateException("Item is not a List");
    return (ConfigList) this;
  }

  /**
   * Checks if this item is a boolean item
   * @return true if boolean item, false otherwise
   */
  public boolean isBoolean() {
    return type.equals(Type.BOOLEAN);
  }

  /**
   * Gets this item as a boolean item
   * @return this as boolean item
   * @throws IllegalStateException if not a boolean item
   */
  public ConfigBoolean asBoolean() {
    if(!isBoolean()) throw new IllegalStateException("Item is not a Boolean");
    return (ConfigBoolean) this;
  }

  /**
   * Checks if this item is a number item
   * @return true if number item, false otherwise
   */
  public boolean isNumber() {
    return type.equals(Type.NUMBER);
  }

  /**
   * Gets this item as a number item
   * @return this as number item
   * @throws IllegalStateException if not a number item
   */
  public ConfigNumber asNumber() {
    if(!isNumber()) throw new IllegalStateException("Item is not a Number");
    return (ConfigNumber) this;
  }

  /**
   * Checks if this item is a string item
   * @return true if string item, false otherwise
   */
  public boolean isString() {
    return type.equals(Type.STRING);
  }

  /**
   * Gets this item as a string item
   * @return this as string item
   * @throws IllegalStateException if not a string item
   */
  public ConfigString asString() {
    if(!isString()) throw new IllegalStateException("Item is not a String");
    return (ConfigString) this;
  }



  /**
   * Gets the type associated with a given {@link JsonElement}.
   * @param value element to check type for
   * @return the type associated with this element. If not known/supported, null is returned.
   */
  @Internal
  public static Type getType(@NotNull JsonElement value) {
    Objects.requireNonNull(value);
    if(value.isJsonArray()) return Type.ARRAY;
    if(value.isJsonObject()) return Type.GROUP;
    if(value.isJsonPrimitive()) {
      var prim = value.getAsJsonPrimitive();
      if(prim.isString()) return Type.STRING;
      if(prim.isBoolean()) return Type.BOOLEAN;
      if(prim.isNumber()) return Type.NUMBER;
    }
    return null;
  }

  /**
   * Creates a new {@link BaseConfigItem} instance based on the {@link BaseConfigItem.Type} given.
   * @param type Type to instantiate.
   * @return new instance of an {@link BaseConfigItem}.
   */
  @Internal
  public static BaseConfigItem<?> getInstance(@NotNull Type type) {
    Objects.requireNonNull(type);
    return getInstance(type, "");
  }

  /**
   * Creates a new {@link BaseConfigItem} instance based on the {@link BaseConfigItem.Type} given.
   * @param type Type to instantiate.
   * @param key key to give item.
   * @return new instance of an {@link BaseConfigItem}.
   */
  @Internal
  public static BaseConfigItem<?> getInstance(@NotNull Type type, @NotNull String key) {
    Objects.requireNonNull(type);
    Objects.requireNonNull(key);
    switch(type){
      case ARRAY:
        return new ConfigList(key, null, null);
      case BOOLEAN:
        return new ConfigBoolean(key, null);
      case GROUP:
        return new ConfigGroup(key, null);
      case NUMBER:
        return new ConfigNumber(key, null);
      case STRING:
        return new ConfigString(key, null);
    }
    return null;
  }

  /**
   * Type representing the value of what is stored in {@link BaseConfigItem}
   * Is a list of all supported types.
   */
  public enum Type {
    /**
     * Used to represent a json object.
     * @see ConfigGroup
     */
    GROUP,
    /**
     * Used to represent a boolean value.
     * @see ConfigBoolean
     */
    BOOLEAN,
    /**
     * Used to represent a string value.
     * @see ConfigString
     */
    STRING,
    /**
     * Used to represent a Numerical value.
     * @see ConfigNumber
     */
    NUMBER,
    /**
     * Used to represent a json array.
     * @see ConfigList
     */
    ARRAY
  }
}
