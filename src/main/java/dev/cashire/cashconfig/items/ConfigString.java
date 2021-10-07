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
package dev.cashire.cashconfig.items;

import java.util.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

/**
 * Configuration item for String values for {@link BaseConfigItem}.
 */
public final class ConfigString extends BaseConfigItem<String> {
  /**
   * Creates an empty String item with no key
   * If adding to a {@link ConfigGroup}, use {@link #ConfigString(String, String)} instead.
   */
  public ConfigString() {
    this("","");
  }

  /**
   * Creates a new String item with given key
   * @param key The key to be used if put in a group.
   */
  public ConfigString(@NotNull String key) {
    this(key, "");
  }

  /**
   * Creates a new String item with given key and value
   * @param key The key to be used if put in a group.
   * @param str The default value contained in this item.
   */
  public ConfigString(@NotNull String key, String str) {
    super(Objects.requireNonNull(key), Type.STRING);
    this.value = str != null ? str : "";
  }

  @Override
  public void toJson(@NotNull JsonObject parent) {
    Objects.requireNonNull(parent);
    parent.addProperty(key, value);
  }

  @Override
  public void toJson(@NotNull JsonArray parent) {
    Objects.requireNonNull(parent);
    parent.add(value);
  }

  @Override
  public void fromJson(@NotNull JsonElement element) {
    Objects.requireNonNull(element);
    value = element.getAsString();
    
  }
}
