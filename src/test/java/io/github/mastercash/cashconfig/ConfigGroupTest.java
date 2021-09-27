package io.github.mastercash.cashconfig;

import org.junit.Test;
import org.junit.Assert;

import io.github.mastercash.cashconfig.Items.ConfigBoolean;
import io.github.mastercash.cashconfig.Items.ConfigGroup;
import io.github.mastercash.cashconfig.Items.ConfigList;
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
    test.AddItem(new ConfigBoolean("test", true));
    Assert.assertEquals("Added value", true, test.GetItem("test").getValue());
  }

  @Test
  public void toJSONEmpty() {
    var test = new ConfigGroup("test", null);
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
    var test = new ConfigGroup("test", null);
    var json = new JsonObject();
    test.fromJson(json);
    Assert.assertEquals("Empty Group from JSON is Empty", true, test.size() == 0);
  }

  @Test
  public void fromJSONFilled() {
    var test = new ConfigGroup("test", null);
    var json = new JsonObject();
    json.addProperty("str", "test");
    var sub = new JsonObject();
    sub.addProperty("test", true);
    json.add("obj", sub);
    test.fromJson(json);
    Assert.assertEquals("Filled Group from JSON has items", true, test.size() > 0);
    Assert.assertEquals("Filled Group from JSON correct value", "test", (String) test.GetItem("str").getValue());
    Assert.assertEquals("Filled Group from JSON nested Object", true, (Boolean) ((ConfigGroup) test.GetItem("obj")).GetItem("test").getValue());
  }

}
