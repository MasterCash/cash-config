# Cash Config
other config platforms didn't do what I wanted so I am doing it myself

## Description
This is a simple Fabric Server side mod to allow saving and loading configuration data to a file.

Shout out to [OroArmor](https://github.com/OroArmor) and their [Oro-Config Mod](https://github.com/OroArmor/Oro-Config) for providing heavy influence for this mod.

## Usage
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

Usage: `getValue()`
##### `key`
returns the key string used to identify this item in a JSON object. This field is required if the item will be in a group. This field is not required for items being added to a JSON array. If being added to an array, `""` is a valid value.

Usage: `getKey()`

All ConfigItems live under: 
`io.github.mastercash.cashconfig.items.*`

#### `ConfigBoolean`
----
Type enum: `Type.BOOLEAN`

This type is used for storing/loading boolean values in a configuration. 

Usage:
```java
// if being added to a group
var item = new ConfigBoolean(<key>,<value>);
// if being added to an array
var item = new ConfigBoolean("",<value>);
```

#### `ConfigString`
---
Type enum: `Type.STRING`

This type is used for storing/loading string values in a configuration. 

Usage:
```java
// if being added to a group
var item = new ConfigString(<key>,<value>);
// if being added to an array
var item = new ConfigString("",<value>);
```

#### `ConfigNumber`
---
Type enum: `Type.NUMBER`

This type is used for storing/loading numeric values in a configuration. 

Usage:
```java
// if being added to a group
var item = new ConfigNumber(<key>,<value>);
// if being added to an array
var item = new ConfigNumber("",<value>);
```
#### `ConfigGroup`
---
Type enum: `Type.GROUP`

This type is used for storing/loading objects in a configuration. This type stores any subclass of `BaseConfigItem`. Items given to this object must have a `key` that is distinct from the other items in this group.

Nesting of Arrays and Objects is supported.

Usage:
```java
// if being added to a group
var item = new ConfigGroup(<key>,<list of Items>);
// if being added to an array
var item = new ConfigGroup("",<list of items>);
// also acceptable to give null and add the items later
var item = new ConfigGroup(<key>, null);
var item = new ConfigGroup("", null);
```
Additional Methods:
```java
// gets the number of items in this group
item.size();
// add an item to the group, the key of the item needs to be unique to this group
item.AddItem(<item>);
// checks to see if a key exists currently in the group
item.HasItem(<key>);
// gets the item with the given key in the group
item.GetItem(<key>);
```

#### `ConfigList`
---
Type enum: `Type.ARRAY`

This type is used for storing/loading arrays in a configuration. This type stores any subclass of `BaseConfigItem`. Items given to this object don't have a `key` and this value is not used for this type. Although, this type doesn't support multi-type variable storage. 

This type has a field `subType` of `Type`. This represents the type inside the array. If this type is not given at creation, it is deduced from the first item added to the list.

Usage:
```java
// if being added to a group
var item = new ConfigList(<key>,<list of Items>);
// if being added to an array
var item = new ConfigList("",<list of items>);
```
Additional Methods:
```java
// gets the number of items in this group
item.size();
// add an item to the group, the key of the item needs to be unique to this group
item.AddItem(<item>);
// checks to see if a key exists currently in the group
item.HasItem(<key>);
// gets the item with the given key in the group
item.GetItem(<key>);
// gets the type of the items in the array (if array empty possibly null)
item.getSubType();
```


### Config
The main entry point is the `Config` class. This lives in the `io.github.mastercash.cashconfig`.
This class acts as the root of the config file. It handles loading and saving the configuration data.
Currently, data cannot be changed but a default can be built and saved if file doesn't exist.

```java
// Creates a new config with the given defaults to be saved if file doesn't exist.
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
```