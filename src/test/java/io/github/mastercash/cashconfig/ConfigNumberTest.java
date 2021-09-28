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

import org.junit.Assert;
import org.junit.Test;

import io.github.mastercash.cashconfig.Items.ConfigNumber;

public class ConfigNumberTest {
  
  @Test
  public void toJSONToInt() {
    var test = new ConfigNumber("test",10);
    var json = new JsonObject();
    test.toJson(json);
    Assert.assertEquals("Int Number to JSON", 10, json.get("test").getAsNumber().intValue());
  }

  @Test
  public void toJSONToFloat() {
    var test = new ConfigNumber("test",10.2f);
    var json = new JsonObject();
    test.toJson(json);
    Assert.assertEquals("Int Number to JSON", 10.2f, json.get("test").getAsNumber().floatValue(), 0.0002);
  }
}
