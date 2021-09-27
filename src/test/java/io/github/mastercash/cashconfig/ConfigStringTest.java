package io.github.mastercash.cashconfig;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.junit.Assert;
import org.junit.Test;

import io.github.mastercash.cashconfig.Items.ConfigString;

public class ConfigStringTest {
  
  @Test
  public void toJSON() {
    var test = new ConfigString("test", "test");
    var json = new JsonObject();
    test.toJson(json);
    Assert.assertEquals("String to JSON", "test", json.get("test").getAsString());
  }

  @Test
  public void fromJSON() {
    var test = new ConfigString();
    test.fromJson(new JsonPrimitive("test"));
    Assert.assertEquals("String from JSON", "test", test.getValue());
  }
}