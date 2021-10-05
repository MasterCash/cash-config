package io.github.mastercash.cashconfig;

import java.io.File;

import org.apache.commons.compress.archivers.dump.DumpArchiveEntry.TYPE;
import org.junit.Assert;
import org.junit.Test;

import io.github.mastercash.cashconfig.Items.ConfigGroup;
import io.github.mastercash.cashconfig.Items.ConfigString;
import io.github.mastercash.cashconfig.Items.BaseConfigItem.Type;
import static com.google.common.collect.ImmutableList.of;

public class ConfigTest {
  private File file = new File("test.json");
  private ConfigString str(String key) {return new ConfigString(key, "test"); }
  @Test
  public void CreateConfig() {
    var test = new Config(str("test"), file);
  }
  
  @Test
  public void GetItem() {
    var test = new Config(str("test"), file);
    Assert.assertEquals("test", test.getItem("test", Type.STRING).getValue());
  }

  @Test
  public void GetGroup() {
    var grp = new ConfigGroup("test", of(str("test"), str("other")));
    var test = new Config(grp, file);
    Assert.assertNotNull(test.getItem("test", Type.GROUP));
    Assert.assertEquals("test", test.getItem("test.test", Type.STRING).getValue());
    Assert.assertEquals("test", test.getItem("test.other", Type.STRING).getValue());
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
    test.RemoveItem("test");
    Assert.assertEquals(null, test.get);
  }
}
