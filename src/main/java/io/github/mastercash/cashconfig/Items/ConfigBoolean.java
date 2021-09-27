package io.github.mastercash.cashconfig.Items;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public final class ConfigBoolean extends BaseConfigItem<Boolean> {

  public ConfigBoolean() {
    this("", false);
  }

  public ConfigBoolean(String key, Boolean value) {
    super(key, Type.BOOLEAN);
    this.value = value != null ? value : false;
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
    value = element.getAsBoolean();
  }
  
}
