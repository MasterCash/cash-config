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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.Nullable;

public abstract class BaseConfigItem<T> {
  protected final String key;
  @Nullable
  protected T value;
  protected final Type type;
  
  public abstract void toJson(JsonObject parent); 
  public abstract void toJson(JsonArray parent);
  public abstract void fromJson(JsonElement element);

  public Type getType() {
    return type;
  }
  public String getKey() {
    return key;
  }
  @Nullable
  public T getValue() {
    return value;
  }

  protected static <T> boolean validType(JsonElement element, Class<T> clazz) {
    if(element.isJsonObject()) {
      return clazz.equals(ConfigGroup.class);
    }
    if(element.isJsonArray()) {
      return clazz.equals(ConfigList.class);
    }
    if(element.isJsonPrimitive()) {
      var prim = element.getAsJsonPrimitive();
      if(prim.isNumber()) {
        return clazz.equals(ConfigNumber.class);
      }
      if(prim.isString()) {
        return clazz.equals(ConfigString.class);
      }
      if(prim.isBoolean()) {
        return clazz.equals(ConfigBoolean.class);
      }
    }
    return false;
  }

  protected static boolean validType(JsonElement element, Type type) {
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

  protected BaseConfigItem(String key, Type type) {
    this.key = key;
    this.type = type;
  }

  public static Type getType(JsonElement value) {
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
  public static BaseConfigItem<?> getInstance(Type type) {
    return getInstance(type, "");
  }
  public static BaseConfigItem<?> getInstance(Type type, String key) {
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

  public enum Type {
    GROUP,
    BOOLEAN,
    STRING,
    NUMBER,
    ARRAY
  }
}
