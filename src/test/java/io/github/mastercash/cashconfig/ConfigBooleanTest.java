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
  
}
