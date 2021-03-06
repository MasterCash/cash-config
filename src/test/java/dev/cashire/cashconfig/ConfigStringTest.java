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

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.cashire.cashconfig.items.ConfigString;
import org.junit.Assert;
import org.junit.Test;


/**
 * Junit Test for {@link ConfigString}.
 */
public class ConfigStringTest {
  
  @Test
  public void toJson() {
    var test = new ConfigString("test", "test");
    var json = new JsonObject();
    test.toJson(json);
    Assert.assertEquals("String to JSON", "test", json.get("test").getAsString());
  }

  @Test
  public void fromJson() {
    var test = new ConfigString();
    test.fromJson(new JsonPrimitive("test"));
    Assert.assertEquals("String from JSON", "test", test.getValue());
  }

  @Test
  public void setValue() {
    var test = new ConfigString();
    test.setValue("test");
    Assert.assertEquals("test", test.getValue());
    test = new ConfigString("", "other");
    Assert.assertEquals("other", test.getValue());
  }

  @Test
  public void isItem() {
    var test = new ConfigString();
    Assert.assertEquals(false, test.isBoolean());
    Assert.assertEquals(false, test.isGroup());
    Assert.assertEquals(false, test.isList());
    Assert.assertEquals(false, test.isNumber());
    Assert.assertEquals(true, test.isString());
  }

  @Test
  public void asItem() {
    var test = new ConfigString();
    Assert.assertEquals(test, test.asString());
    Assert.assertThrows(IllegalStateException.class, () -> test.asBoolean());
    Assert.assertThrows(IllegalStateException.class, () -> test.asGroup());
    Assert.assertThrows(IllegalStateException.class, () -> test.asList());
    Assert.assertThrows(IllegalStateException.class, () -> test.asNumber());
  }

}