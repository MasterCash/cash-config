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

import org.junit.Test;
import org.junit.Assert;

import io.github.mastercash.cashconfig.Items.ConfigBoolean;
import io.github.mastercash.cashconfig.Items.ConfigGroup;
import io.github.mastercash.cashconfig.Items.ConfigList;
import io.github.mastercash.cashconfig.Items.ConfigNumber;
import io.github.mastercash.cashconfig.Items.ConfigString;
import io.github.mastercash.cashconfig.Items.BaseConfigItem.Type;
import static com.google.common.collect.ImmutableList.of;
import com.google.gson.JsonObject;

public class ConfigGroupTest {
  
  @Test
  public void createGroupConstructor() {
    var test = new ConfigGroup("test", of(new ConfigBoolean("1", false), new ConfigBoolean("2", false)));
    Assert.assertEquals("Constructed with values", true, test.getValue().size() == 2);
  }
  @Test
  public void createGroupAddValues() {
    var test = new ConfigGroup();
    test.addItem(new ConfigBoolean("test", true));
    Assert.assertEquals("Added value", true, test.getItem("test").getValue());
  }

  @Test
  public void toJSONEmpty() {
    var test = new ConfigGroup("test");
    var json = new JsonObject();
    test.toJson(json);
    Assert.assertEquals("Empty Group to JSON is Empty", true, json.get("test").getAsJsonObject().size() == 0);
  }
  
  @Test
  public void toJSONFilled() {
    var test = new ConfigGroup("test", of(new ConfigString("str","test"), new ConfigGroup("obj",of(new ConfigBoolean("test", true))), new ConfigList("list", of(new ConfigBoolean("", true), new ConfigBoolean("", true)), Type.BOOLEAN)));
    var json = new JsonObject();
    test.toJson(json);
    Assert.assertEquals("Filled Group to JSON Has Items", true, json.get("test").getAsJsonObject().size() > 0);
    Assert.assertEquals("Filled Group to JSON correct value", "test", json.get("test").getAsJsonObject().get("str").getAsString());
    Assert.assertEquals("Filled Group to JSON nested Object", true, json.get("test").getAsJsonObject().get("obj").getAsJsonObject().size() > 0);
    Assert.assertEquals("Filled Group to JSON nested Object correct value", true, json.get("test").getAsJsonObject().get("obj").getAsJsonObject().get("test").getAsBoolean());
    Assert.assertEquals("Filled Group to JSON array", true, json.get("test").getAsJsonObject().get("list").getAsJsonArray().size() == 2);
  }

  @Test
  public void fromJSONEmpty() {
    var test = new ConfigGroup("test");
    var json = new JsonObject();
    test.fromJson(json);
    Assert.assertEquals("Empty Group from JSON is Empty", true, test.size() == 0);
  }

  @Test
  public void fromJSONFilled() {
    var test = new ConfigGroup("test");
    var json = new JsonObject();
    json.addProperty("str", "test");
    var sub = new JsonObject();
    sub.addProperty("test", true);
    json.add("obj", sub);
    test.fromJson(json);
    Assert.assertEquals("Filled Group from JSON has items", true, test.size() > 0);
    Assert.assertEquals("Filled Group from JSON correct value", "test", (String) test.getItem("str").getValue());
    Assert.assertEquals("Filled Group from JSON nested Object", true, (Boolean) ((ConfigGroup) test.getItem("obj")).getItem("test").getValue());
  }

  @Test
  public void removeItem() {
    var test = new ConfigGroup();
    test.addItem(new ConfigString("test", "test"));
    test.removeItem("test");
    Assert.assertEquals("Removed item", 0, test.size());
  }

  @Test
  public void addItem() {
    var test = new ConfigGroup();
    var str = new ConfigString("test", "test");
    test.addItem(str);
    Assert.assertEquals(true, test.hasItem("test"));
    Assert.assertEquals(1, test.size());
    Assert.assertEquals(str, test.getItem("test"));
  }

  @Test
  public void setItem() {
    var str = new ConfigString("test", "test");
    var num = new ConfigNumber("test", 0);
    var test = new ConfigGroup("", of(str));
    test.setItem(num);
    Assert.assertEquals(num, test.getItem("test"));
  }

  @Test
  public void isItem() {
    var test = new ConfigGroup();
    Assert.assertEquals(false, test.isBoolean());
    Assert.assertEquals(true, test.isGroup());
    Assert.assertEquals(false, test.isList());
    Assert.assertEquals(false, test.isNumber());
    Assert.assertEquals(false, test.isString());
  }

  @Test
  public void asItem() {
    var test = new ConfigGroup();
    Assert.assertEquals(test, test.asGroup());
    Assert.assertThrows(IllegalStateException.class, () -> test.asBoolean());
    Assert.assertThrows(IllegalStateException.class, () -> test.asList());
    Assert.assertThrows(IllegalStateException.class, () -> test.asNumber());
    Assert.assertThrows(IllegalStateException.class, () -> test.asString());
  }
}
