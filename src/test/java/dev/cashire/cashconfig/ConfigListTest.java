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

import static com.google.common.collect.ImmutableList.of;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.cashire.cashconfig.items.BaseConfigItem.Type;
import dev.cashire.cashconfig.items.ConfigBoolean;
import dev.cashire.cashconfig.items.ConfigGroup;
import dev.cashire.cashconfig.items.ConfigList;
import dev.cashire.cashconfig.items.ConfigString;
import org.junit.Assert;
import org.junit.Test;

/**
 * Junit Test for {@link ConfigList}.
 */
public class ConfigListTest {

  @Test
  public void createListConstructor() {
    var test = new ConfigList(
        "test", 
        of(new ConfigBoolean(), new ConfigBoolean(), new ConfigBoolean()), null);
    Assert.assertEquals("list constructor", 3, test.getValue().size());
  }

  @Test
  public void createListAdd() {
    var test = new ConfigList();
    test.addItem(new ConfigBoolean());
    Assert.assertEquals("add item", true, test.getValue().size() > 0);
  }

  @Test
  public void invalidTypes() {
    Assert.assertThrows("adding invalid type constructor",
        IllegalArgumentException.class, () -> new ConfigList("test",
        of(new ConfigBoolean(), new ConfigString()), Type.BOOLEAN)); 
    var test = new ConfigList("test", null, Type.BOOLEAN);
    Assert.assertThrows("adding invalid type to list", 
        IllegalArgumentException.class, () -> test.addItem(new ConfigString()));
  }

  @Test
  public void createNestedArrays() {
    var test = new ConfigList("list", of(new ConfigList(), new ConfigList()), Type.ARRAY);
    Assert.assertEquals("Has lists", true, test.getValue().size() > 0);
  }

  @Test
  public void toJsonEmpty() {
    var test = new ConfigList("test");
    var json = new JsonObject();
    test.toJson(json);
    Assert.assertEquals("List to JSON empty", true, json.get("test").getAsJsonArray().size() == 0);
  }

  @Test
  public void toJsonFilled() {
    var test = new ConfigList("test", 
        of(new ConfigBoolean(), new ConfigBoolean(), new ConfigBoolean()), Type.BOOLEAN);
    var json = new JsonObject();
    test.toJson(json);
    Assert.assertEquals("List to JSON has elements", 3, json.get("test").getAsJsonArray().size());
  }
  
  @Test
  public void toJsonFilledGroupWithArray() {
    var arr = new ConfigList("nestedArray", 
        of(new ConfigBoolean(), new ConfigBoolean(), new ConfigBoolean()), Type.BOOLEAN);
    var obj = new ConfigGroup("", of(arr));
    var test = new ConfigList("test", of(obj), Type.GROUP);
    var json = new JsonObject();
    test.toJson(json);
    Assert.assertEquals("List to JSON Group with List", 3, 
        json.get("test").getAsJsonArray().get(0).getAsJsonObject()
        .get("nestedArray").getAsJsonArray().size());
  }

  @Test
  public void fromJsonEmpty() {
    var test = new ConfigList("", of(new ConfigBoolean(), new ConfigBoolean()), Type.BOOLEAN);
    var json = new JsonArray();
    test.fromJson(json);
    Assert.assertEquals("List from JSON empty", true, test.getValue().size() == 0);
  }

  @Test
  public void fromJsonFilled() {
    var json = new JsonArray();
    json.add(1);
    json.add(1);
    json.add(1);
    var test = new ConfigList();
    test.fromJson(json);
    Assert.assertEquals("List from JSON full", 3, test.getValue().size());
  }

  @Test
  public void fromJsonFilledArrayInArray() {
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
    Assert.assertEquals("List from JSON has List with values", 3, 
        test.getValue().get(0).asList().size());

  }

  @Test
  public void fromJsonFilledGroupWithArray() {
    var list = new JsonArray();
    list.add(false);
    list.add(false);
    list.add(false);
    var obj = new JsonObject();
    obj.add("list", list);
    var json = new JsonArray();
    json.add(obj);
    var test = new ConfigList();
    test.fromJson(json);
    Assert.assertEquals("List from JSON Group with List", 3, 
        test.getValue().get(0).asGroup().getItem("list").asList().size());
  }

  @Test
  public void addItem() {
    var test = new ConfigList();
    var item = new ConfigString("", "Test");
    test.addItem(item);
    Assert.assertEquals(1, test.size());
    Assert.assertEquals(item, test.getValue().get(0));
  }

  @Test
  public void getItem() {
    var item1 = new ConfigString();
    var item2 = new ConfigString();
    var test = new ConfigList("", of(item1), Type.STRING);
    test.addItem(item2);
    Assert.assertEquals(item1, test.getItem(0));
    Assert.assertEquals(item2, test.getItem(1));
  }

  @Test
  public void removeItem() {
    var item1 = new ConfigString();
    var item2 = new ConfigString();
    var test = new ConfigList("", of(item1), Type.STRING);
    test.addItem(item2);
    Assert.assertEquals(item1, test.removeItem(0));
    Assert.assertEquals(1, test.size());
    Assert.assertEquals(item2, test.removeItem(0));
    Assert.assertEquals(0, test.size());
  }

  @Test
  public void isItem() {
    var test = new ConfigList();
    Assert.assertEquals(false, test.isBoolean());
    Assert.assertEquals(false, test.isGroup());
    Assert.assertEquals(true, test.isList());
    Assert.assertEquals(false, test.isNumber());
    Assert.assertEquals(false, test.isString());
  }

  @Test
  public void asItem() {
    var test = new ConfigList();
    Assert.assertEquals(test, test.asList());
    Assert.assertThrows(IllegalStateException.class, () -> test.asBoolean());
    Assert.assertThrows(IllegalStateException.class, () -> test.asGroup());
    Assert.assertThrows(IllegalStateException.class, () -> test.asNumber());
    Assert.assertThrows(IllegalStateException.class, () -> test.asString());
  }
}
