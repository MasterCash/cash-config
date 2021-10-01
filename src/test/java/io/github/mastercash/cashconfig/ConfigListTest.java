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

import org.junit.Assert;
import org.junit.Test;

import io.github.mastercash.cashconfig.Items.ConfigBoolean;
import io.github.mastercash.cashconfig.Items.ConfigGroup;
import io.github.mastercash.cashconfig.Items.ConfigList;
import io.github.mastercash.cashconfig.Items.ConfigString;
import io.github.mastercash.cashconfig.Items.BaseConfigItem.Type;

import static com.google.common.collect.ImmutableList.of;

import java.security.InvalidParameterException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ConfigListTest {

  @Test
  public void createListConstructor() {
    var test = new ConfigList("test", of(new ConfigBoolean(), new ConfigBoolean(), new ConfigBoolean()), null);
    Assert.assertEquals("list constructor", 3, test.getValue().size());
  }

  @Test
  public void createListAdd() {
    var test = new ConfigList();
    test.AddItem(new ConfigBoolean());
    Assert.assertEquals("add item", true, test.getValue().size() > 0);
  }

  @Test
  public void invalidTypes() {
    Assert.assertThrows("adding invalid type constructor", InvalidParameterException.class,() -> new ConfigList("test", of(new ConfigBoolean(), new ConfigString()),Type.BOOLEAN)); 
    var test = new ConfigList("test",null,Type.BOOLEAN);
    Assert.assertThrows("adding invalid type to list", InvalidParameterException.class,() -> test.AddItem(new ConfigString()));
  }

  @Test
  public void createNestedArrays() {
    var test = new ConfigList("list", of(new ConfigList(), new ConfigList()), Type.ARRAY);
    Assert.assertEquals("Has lists", true, test.getValue().size() > 0);
  }

  @Test
  public void toJSONEmpty() {
    var test = new ConfigList("test", null, null);
    var json = new JsonObject();
    test.toJson(json);
    Assert.assertEquals("List to JSON empty", true, json.get("test").getAsJsonArray().size() == 0);
  }

  @Test
  public void toJSONFilled() {
    var test = new ConfigList("test", of(new ConfigBoolean(), new ConfigBoolean(), new ConfigBoolean()), Type.BOOLEAN);
    var json = new JsonObject();
    test.toJson(json);
    Assert.assertEquals("List to JSON has elements", 3, json.get("test").getAsJsonArray().size());
  }
  
  @Test
  public void toJSONFilledGroupWithArray() {
    var arr = new ConfigList("nestedArray", of(new ConfigBoolean(), new ConfigBoolean(), new ConfigBoolean()), Type.BOOLEAN);
    var obj = new ConfigGroup("", of(arr));
    var test = new ConfigList("test", of(obj), Type.GROUP);
    var json = new JsonObject();
    test.toJson(json);
    Assert.assertEquals("List to JSON Group with List", 3, json.get("test").getAsJsonArray().get(0).getAsJsonObject().get("nestedArray").getAsJsonArray().size());
  }

  @Test
  public void fromJSONEmpty() {
    var test = new ConfigList("", of(new ConfigBoolean(), new ConfigBoolean()), Type.BOOLEAN);
    var json = new JsonArray();
    test.fromJson(json);
    Assert.assertEquals("List from JSON empty", true, test.getValue().size() == 0);
  }

  @Test
  public void fromJSONFilled() {
    var test = new ConfigList();
    var json = new JsonArray();
    json.add(1);
    json.add(1);
    json.add(1);
    test.fromJson(json);
    Assert.assertEquals("List from JSON full", 3, test.getValue().size());
  }

  @Test
  public void fromJSONFilledArrayInArray() {
    var list = new JsonArray();
    list.add(1);
    list.add(1);
    list.add(1);
    var json = new JsonArray();
    json.add(list);
    json.add(list);
    json.add(list);
    var test = new ConfigList();
    test.fromJson(json);
    Assert.assertEquals("List from JSON has Lists", 3, test.size());
    Assert.assertEquals("List from JSON is Lists", Type.ARRAY, test.getValue().get(0).getType());
    Assert.assertEquals("List from JSON has List with values", 3, ((ConfigList) test.getValue().get(0)).size());

  }

  @Test
  public void fromJSONFilledGroupWithArray() {
    var obj = new JsonObject();
    var list = new JsonArray();
    list.add(false);
    list.add(false);
    list.add(false);
    obj.add("list", list);
    var json = new JsonArray();
    json.add(obj);
    var test = new ConfigList();
    test.fromJson(json);
    Assert.assertEquals("List from JSON Group with List", 3, ((ConfigList)((ConfigGroup) test.getValue().get(0)).GetItem("list")).size());
  }

  @Test
  public void addItem() {
    var test = new ConfigList();
    var item = new ConfigString("", "Test");
    test.AddItem(item);
    Assert.assertEquals(1, test.size());
    Assert.assertEquals(item, test.getValue().get(0));
  }

  @Test
  public void getItem() {
    var item1 = new ConfigString();
    var item2 = new ConfigString();
    var test = new ConfigList("", of(item1), Type.STRING);
    test.AddItem(item2);
    Assert.assertEquals(item1, test.GetItem(0));
    Assert.assertEquals(item2, test.GetItem(1));
  }

  @Test
  public void removeItem() {
    var item1 = new ConfigString();
    var item2 = new ConfigString();
    var test = new ConfigList("", of(item1), Type.STRING);
    test.AddItem(item2);
    Assert.assertEquals(item1, test.RemoveItem(0));
    Assert.assertEquals(1, test.size());
    Assert.assertEquals(item2, test.RemoveItem(0));
    Assert.assertEquals(0, test.size());
  }
}
