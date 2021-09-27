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
