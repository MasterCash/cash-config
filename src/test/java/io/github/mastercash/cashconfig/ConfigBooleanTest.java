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

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.junit.Assert;
import org.junit.Test;

import io.github.mastercash.cashconfig.Items.ConfigBoolean;

public class ConfigBooleanTest {
  @Test
  public void fromJSON() {
    var test = new ConfigBoolean();
    test.fromJson(new JsonPrimitive(true));
    Assert.assertEquals("Correct value from json", (Boolean) true, test.getValue());
  }

  @Test
  public void toJSON() {
    var test = new ConfigBoolean("test", true);
    var obj = new JsonObject();
    test.toJson(obj);
    var bool = obj.get("test").getAsJsonPrimitive().getAsBoolean();
    Assert.assertEquals("Has Bool in JSON", true, bool);
  }

  @Test
  public void changeValue() {
    var test = new ConfigBoolean();
    test.setValue(true);
    Assert.assertEquals(true, test.getValue());
  }
  
}
