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
import dev.cashire.cashconfig.items.ConfigNumber;
import org.junit.Assert;
import org.junit.Test;


/**
 * Junit Test for {@link ConfigNumber}.
 */
public class ConfigNumberTest {
  
  @Test
  public void toJsonToInt() {
    var test = new ConfigNumber("test", 10);
    var json = new JsonObject();
    test.toJson(json);
    Assert.assertEquals("Int Number to JSON", 10, json.get("test").getAsNumber().intValue());
  }

  @Test
  public void toJsonToFloat() {
    var test = new ConfigNumber("test", 10.2f);
    var json = new JsonObject();
    test.toJson(json);
    Assert.assertEquals("Int Number to JSON", 10.2f, json.get("test").getAsNumber().floatValue(), 0.0002);
  }

  @Test
  public void setValue() {
    var test = new ConfigNumber("", 10);
    test.setValue(11);
    Assert.assertEquals(11, test.getValue());
    test = new ConfigNumber();
    test.setValue(5);
    Assert.assertEquals(5, test.getValue());
  }

  @Test
  public void isItem() {
    var test = new ConfigNumber();
    Assert.assertEquals(false, test.isBoolean());
    Assert.assertEquals(false, test.isGroup());
    Assert.assertEquals(false, test.isList());
    Assert.assertEquals(true, test.isNumber());
    Assert.assertEquals(false, test.isString());
  }

  @Test
  public void asItem() {
    var test = new ConfigNumber();
    Assert.assertEquals(test, test.asNumber());
    Assert.assertThrows(IllegalStateException.class, () -> test.asBoolean());
    Assert.assertThrows(IllegalStateException.class, () -> test.asGroup());
    Assert.assertThrows(IllegalStateException.class, () -> test.asList());
    Assert.assertThrows(IllegalStateException.class, () -> test.asString());
  }

}
