package io.github.mastercash.cashconfig;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import io.github.mastercash.cashconfig.Items.ConfigGroup;
import io.github.mastercash.cashconfig.Items.ConfigString;
import io.github.mastercash.cashconfig.Items.BaseConfigItem.Type;
import static com.google.common.collect.ImmutableList.of;

public class ConfigTest {
  private static File file = new File("test.json");
  private ConfigString str(String key) {return new ConfigString(key, "test"); }

  @AfterClass
  public static void Cleanup() {
    file.deleteOnExit();
  }

  @Test
  public void CreateConfig() {
    new Config(str("test"), file);
  }
  
  @Test
  public void GetItem() {
    var test = new Config(str("test"), file);
    Assert.assertEquals("test", test.GetItem("test", Type.STRING).getValue());
  }

  @Test
  public void GetGroup() {
    var grp = new ConfigGroup("test", of(str("test"), str("other")));
    var test = new Config(grp, file);
    Assert.assertNotNull(test.GetItem("test", Type.GROUP));
    Assert.assertEquals("test", test.GetItem("test.test", Type.STRING).getValue());
    Assert.assertEquals("test", test.GetItem("test.other", Type.STRING).getValue());
  }

  @Test
  public void SaveItems() {
    var test = new Config(str("test"), file);
    test.saveFile();
  }

  @Test
  public void LoadItems() {

    var old = new Config(of(str("test"), str("other")), file);
    
    old.saveFile(); 
    var test = new Config(new ConfigString("test", "bob"), file);
    test.readFile();
    Assert.assertEquals("test", test.GetItem("test", Type.STRING).getValue());
    Assert.assertEquals("test", test.GetItem("other", Type.STRING).getValue());
  }

  @Test
  public void getMissingItem() {
    var test = new Config(new ConfigGroup("test"), file);
    Assert.assertNull(test.GetItem("bob", Type.GROUP));
    Assert.assertNull(test.GetItem("test.bob", Type.GROUP));
  }

  @Test
  public void getWrongType() {
    var test = new Config(str("test"), file);
    Assert.assertThrows(IllegalArgumentException.class, () -> test.GetItem("test", Type.GROUP));
  }

  @Test
  public void removeItem() {
    var test = new Config(of(str("test"), new ConfigGroup("root", of(str("test")))), file);
    test.RemoveItem("test");
    test.RemoveItem("root.test");
    Assert.assertEquals(false, test.HasItem("test"));
    Assert.assertEquals(false, test.HasItem("root.test"));
    Assert.assertEquals(true, test.HasItem("root"));
  }
}