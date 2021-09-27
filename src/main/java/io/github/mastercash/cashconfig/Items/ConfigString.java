package io.github.mastercash.cashconfig.Items;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class ConfigString extends BaseConfigItem<String> {

  public ConfigString() {
    this("","");
  }
  public ConfigString(String key, String str) {
    super(key, Type.STRING);
    this.value = str != null ? str : "";
  }

  @Override
  public void toJson(JsonObject parent) {
    parent.addProperty(key, value);
  }

  @Override
  public void toJson(JsonArray parent) {
    parent.add(value);
  }

  @Override
  public void fromJson(JsonElement element) {
    value = element.getAsString();
    
  }
  
}
