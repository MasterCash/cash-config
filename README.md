# Cash Config
other config platforms didn't do what I wanted so I am doing it myself

<br>

![Mod Loader fabric](https://img.shields.io/badge/Mod%20Loader-Fabric-green) ![GitHub all releases](https://img.shields.io/github/downloads/mastercash/cash-config/total) ![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/mastercash/cash-config)

![Supported Versions](https://img.shields.io/badge/Minecraft%20Versions-1.17-informational)

## Description
This is a simple Fabric Server side mod to allow saving and loading configuration data to a file.

Shout out to [OroArmor](https://github.com/OroArmor) and their [Oro-Config Mod](https://github.com/OroArmor/Oro-Config) for providing heavy influence for this mod.

## Usage

<br>

### Adding to Your Project
To add this package to your project it will require having a [github personal token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token). Your token will need to have `read:packages` permission.

The recommended way is to store your token in an environment variable to be used in your build.gradle:
```gradle
repositories {
	maven {
		url = "https://maven.pkg.github.com/mastercash/cash-config"
		credentials {
			username = System.getenv("GITHUB_ACTOR")
			password = System.getenv("GITHUB_TOKEN")
		}
	}
}

dependencies {
  modImplementation "dev.cashire:cash-config:${project.config_version}"
}
```

here `GITHUB_ACTOR` is your github username and `GITHUB_TOKEN` is your personal token.

Note: if you are using Github Actions to build you will need to add a env variable to your gradle build step:
```yml
env:
  GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```


Example:
```yml
- name: build
  run: ./gradlew build
  env:
    GITHUB_TOKEN: $${{ secrets.GITHUB_TOKEN }}
```


<br>

### Types
#### `Type`
---
an enum value to represent which `BaseConfigItem`

Values: `ARRAY`, `GROUP`, `BOOLEAN`, `STRING`, and `NUMBER`


There are a few types used to hold data:
#### `BaseConfigItem`
---
This is the base type that all other configuration types are based off this type, this type can't be instantiated.

Configuration items have three important values: `type`, `value`, and `key`.

##### `type`
returns the `Type` enum of the implementation of this class

Usage: `getType()`

##### `value`
returns the value being stored by configuration. The type of this value is based on the `type`.

Usage: `getValue()`, `setValue(value)`
##### `key`
returns the key string used to identify this item in a JSON object. This field is required if the item will be in a group. This field is not required for items being added to a JSON array. If being added to an array, `""` is a valid value.

Usage: `getKey()`

<br>

Additionally, the base class has helpful functions for casting and verifying the type of item this is:
```java
// These check if the item is a certain type
item.isGroup()
item.isList()
item.isList(subType)
item.isNumber()
item.isBoolean()
item.isString()

// These cast item as a certain type, throws IllegalStateException if casted as the wrong type.
item.asGroup()
item.asList()
item.asNumber()
item.asBoolean()
item.asString()
```

<br>

All ConfigItems live under: 
`dev.cashire.cashconfig.items.*`

<br/>

#### `ConfigBoolean`
----
Type enum: `Type.BOOLEAN`

This type is used for storing/loading boolean values in a configuration. 

Usage:
```java
// if being added to a group
var item = new ConfigBoolean(<key>);
var item = new ConfigBoolean(<key>, <value>);
// if being added to an array
var item = new ConfigBoolean();
var item = new ConfigBoolean("", <value>);
```

<br/>

#### `ConfigString`
---
Type enum: `Type.STRING`

This type is used for storing/loading string values in a configuration. 

Usage:
```java
// if being added to a group
var item = new ConfigString(<key>);
var item = new ConfigString(<key>, <value>);
// if being added to an array
var item = new ConfigString();
var item = new ConfigString("", <value>);
```

<br/>

#### `ConfigNumber`
---
Type enum: `Type.NUMBER`

This type is used for storing/loading numeric values in a configuration. 

Usage:
```java
// if being added to a group
var item = new ConfigNumber(<key>);
var item = new ConfigNumber(<key>, <value>);
// if being added to an array
var item = new ConfigNumber();
var item = new ConfigNumber("",<value>);
```

<br/>

#### `ConfigGroup`
---
Type enum: `Type.GROUP`

This type is used for storing/loading objects in a configuration. This type stores any subclass of `BaseConfigItem`. Items given to this object must have a `key` that is distinct from the other items in this group.

Nesting of Arrays and Objects is supported.

Note: `getValue()` returns an Immutable version of the list of items within. to add an item use `AddItem()`

Usage:
```java
// if being added to a group
var item = new ConfigGroup(<key>);
var item = new ConfigGroup(<key>, <list of Items>);
// if being added to an array
var item = new ConfigGroup();
var item = new ConfigGroup("", <list of items>);
// also acceptable to give null
var item = new ConfigGroup(<key>, null);
var item = new ConfigGroup("", null);
```
Additional Methods:
```java
// gets the number of items in this group
item.size();
// add an item to the group, the key of the item needs to be unique to this group
item.addItem(<item>);
// checks to see if a key exists currently in the group
item.hasItem(<key>);
// gets the item with the given key in the group
item.getItem(<key>);
// sets the given item in the group, will overwrite existing value
item.setItem(<item>);
// remove the item at the given key from the group
item.removeItem(<key>);
```

<br>

#### `ConfigList`
---
Type enum: `Type.ARRAY`

This type is used for storing/loading arrays in a configuration. This type stores any subclass of `BaseConfigItem`. Items given to this object don't have a `key` and this value is not used for this type. Although, this type doesn't support multi-type variable storage. 

This type has a field `subType` of `Type`. This represents the type inside the array. If this type is not given at creation, it is deduced from the first item added to the list.

Note: `getValue()` returns an Immutable version of the list of items within. to add an item use `AddItem()`

Usage:
```java
// if being added to a group
var item = new ConfigList(<key>);
var item = new ConfigList(<key>, <item>);
var item = new ConfigList(<key>, <list of Items>, <type of items>);
// if being added to an array
var item = new ConfigList();
var item = new ConfigList("", <item>);
var item = new ConfigList("", <list of items>, <type of items>);
```
Additional Methods:
```java
// gets the number of items in this list
item.size();
// add an item to the list, the key of the item doesn't matter
item.addItem(<item>);
// gets the item at the given index in the list
item.getItem(<index>);
// removes the item at the given index in the list
item.removeItem(<index>);
// gets the type of the items in the array (if array empty possibly null)
item.getSubType();
```

<br>

### Config
The main entry point is the `Config` class. This lives in the `dev.cashire.cashconfig`.
This class acts as the root of the config file. It handles loading and saving the configuration data.
Currently, data cannot be changed but a default can be built and saved if file doesn't exist.

```java
// Creates a new config with the given defaults to be saved if file doesn't exist.
var config = Config(<item>, <File>);
var config = Config(<list of items>, <File>);
// Will save the file with the current data (useful for overriding current data with a new set).
config.saveFile();
// Will check to see if the file exists already.
config.hasFile();
// Will read data into the configuration file.
// Note: if a root item given at construction is not in the file, it is kept, 
// otherwise the item is overwritten with data from the file.
config.readFile();
// Will get the item at a given path. A path is in the form (<key>.<key>.<key>). 
// Type is the type expected to be at the end of the path. throws IllegalArgumentException if wrong type.
config.getItem(<path>, <type>);
// Checks to see if given path resolves to an item.
config.hasItem(<path>);
// Gets the type associated with the item at the given path. if item doesn't exist, null is returned
config.getType(<path>);
// Removes the item at a given path. 
config.removeItem(<path>);
```
