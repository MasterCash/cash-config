package dev.cashire.cashconfig;

import static com.google.common.collect.ImmutableList.of;

import dev.cashire.cashconfig.items.BaseConfigItem.Type;
import dev.cashire.cashconfig.items.ConfigGroup;
import dev.cashire.cashconfig.items.ConfigString;
import java.io.File;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;


/**
 * Junit Test for {@link Config}.
 */
public class ConfigTest {
  private static File file = new File("test.json");

  private static ConfigString str(String key) {
    return new ConfigString(key, "test");
  }

  @AfterClass
  public static void cleanup() {
    file.deleteOnExit();
  }

  @Test
  public void createConfig() {
    new Config(str("test"), file);
    new Config(file);
    new Config(of(str("test")), file);
  }
  
  @Test
  public void getItem() {
    var test = new Config(str("test"), file);
    Assert.assertEquals("test", test.getItem("test", Type.STRING).getValue());
  }

  @Test
  public void getGroup() {
    var grp = new ConfigGroup("test", of(str("test"), str("other")));
    var test = new Config(grp, file);
    Assert.assertNotNull(test.getItem("test", Type.GROUP));
    Assert.assertEquals("test", test.getItem("test.test", Type.STRING).getValue());
    Assert.assertEquals("test", test.getItem("test.other", Type.STRING).getValue());
  }

  @Test
  public void saveItems() {
    var test = new Config(str("test"), file);
    test.saveFile();
  }

  @Test
  public void loadItems() {

    var old = new Config(of(str("test"), str("other")), file);
    
    old.saveFile(); 
    var test = new Config(new ConfigString("test", "bob"), file);
    test.readFile();
    Assert.assertEquals("test", test.getItem("test", Type.STRING).getValue());
    Assert.assertEquals("test", test.getItem("other", Type.STRING).getValue());
  }

  @Test
  public void getMissingItem() {
    var test = new Config(new ConfigGroup("test"), file);
    Assert.assertNull(test.getItem("bob", Type.GROUP));
    Assert.assertNull(test.getItem("test.bob", Type.GROUP));
  }

  @Test
  public void getWrongType() {
    var test = new Config(str("test"), file);
    Assert.assertThrows(IllegalArgumentException.class, () -> test.getItem("test", Type.GROUP));
  }

  @Test
  public void removeItem() {
    var test = new Config(of(str("test"), new ConfigGroup("root", of(str("test")))), file);
    test.removeItem("test");
    test.removeItem("root.test");
    Assert.assertEquals(false, test.hasItem("test"));
    Assert.assertEquals(false, test.hasItem("root.test"));
    Assert.assertEquals(true, test.hasItem("root"));
  }

  @Test
  public void setItemRoot() {
    var test = new Config(file);
    test.setItem(str("test"));
    Assert.assertEquals(true, test.hasItem("test"));
    test.setItem(new ConfigString("test", "bob"));
    Assert.assertEquals("bob", test.getItem("test").getValue());
  }

  @Test
  public void setItemPath() {
    var test = new Config(new ConfigGroup("test"), file);
    test.setItem("test", str("test"));
    Assert.assertEquals(true, test.hasItem("test.test"));
    test.setItem("test", new ConfigString("test", "bob"));
    Assert.assertEquals("bob", test.getItem("test.test").getValue());
  }

  @Test
  public void addItem() {
    var test = new Config(file);
    test.addItem(new ConfigGroup("test"));
    Assert.assertEquals(true, test.hasItem("test"));
    test.addItem("test", str("test"));
    Assert.assertEquals(true, test.hasItem("test.test"));
  }
}
