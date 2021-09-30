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
import com.google.gson.JsonPrimitive;

import org.jetbrains.annotations.ApiStatus.Internal;
import io.github.mastercash.cashconfig.Config;

/**
 * Configuration item for Numerical values for {@link BaseConfigItem}
 */
public final class ConfigNumber extends BaseConfigItem<Number> {

  /**
   * Creates an empty Number item with no key
   * @deprecated Should not be used outside of {@link Config} or {@link BaseConfigItem} classes/subclasses. Use {@link #ConfigNumber(String, Number)} instead.
   */
  @Internal
  public ConfigNumber() {
    this("", 0);
  }

  /**
   * Creates a new Number item with given key and value
   * @param key The key to be used if put in a group.
   * @param value The default value contained in this item.
   */
  public ConfigNumber(String key, Number value) {
    super(key, Type.NUMBER);
    this.value = value != null ? value : 0;
  }

  @Override
  public void toJson(JsonObject parent) {
    parent.add(key, new JsonPrimitive(value));
  }

  @Override
  public void toJson(JsonArray parent) {
    parent.add(new JsonPrimitive(value));
  }

  @Override
  public void fromJson(JsonElement element) {
    value = element.getAsNumber();
  }
  
}
