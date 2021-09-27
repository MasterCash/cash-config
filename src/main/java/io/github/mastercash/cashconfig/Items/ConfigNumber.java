package io.github.mastercash.cashconfig.Items;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public final class ConfigNumber extends BaseConfigItem<Number> {

  public ConfigNumber() {
    this("", 0);
  }
  public ConfigNumber(String key, Number value) {
    super(key, Type.NUMBER);
    this.value = value != null ? value : 0;
  }

  @Override
  public void toJson(JsonObject parent) {
    parent.add(key, new JsonPrimitive(value));
  }

  @Override
  public void toJson(JsonArray parent) {
    parent.add(new JsonPrimitive(value));
  }

  @Override
  public void fromJson(JsonElement element) {
    value = element.getAsNumber();
  }
  
}
