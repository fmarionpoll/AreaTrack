# Maven template repository for Icy plugins.

![Icy logo](static/icy_imaging_software_logo-2.png)  
<!-- badges: start -->
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)  
[![Twitter](https://img.shields.io/twitter/follow/Icy_BioImaging?style=social)](https://twitter.com/Icy_BioImaging)  
[![Image.sc forum](https://img.shields.io/badge/discourse-forum-brightgreen.svg?style=flat)](https://forum.image.sc/tag/icy)  
<!-- badges: end -->

This is the repository for the source code of *Maven template plugin*, a plugin for the [bioimage analysis software Icy](http://icy.bioimageanalysis.org/), which was developed by members or former members of the [Biological Image Analysis unit at Institut Pasteur](https://research.pasteur.fr/en/team/bioimage-analysis/). This plugin is licensed under GPL3 license.     
Icy is developed and maintained by [Biological Image Analysis unit at Institut Pasteur](https://research.pasteur.fr/en/team/bioimage-analysis/). The [source code of Icy](https://gitlab.pasteur.fr/bia/icy) is also licensed under a GPL3 license.     


## Plugin description

This repository serves as a template for your future Icy plugin. If you want to create a new Icy plugin using **maven** for dependency management, just clone this repository, and edit it to your needs. The following gives hints about what to edit and how.

A more detailed user documentation can be found on the Maven template plugin documentation page on the Icy website: http://icy.bioimageanalysis.org/developer/create-a-new-icy-plugin/

## What to edit.

### The `pom.xml` file.

The `pom` for Icy plugin is fairly standard. First it inherits from `parent-pom-plugin` that specifies a few default values used throughout the Icy application.

You want to edit the first section on the plugin information:

```xml
 <!-- Project Information -->
<artifactId>plugin-template</artifactId>
```

The `artefactId` will be the name of the jar file produced by the compilation of the plugin. Put a sensible name for it since this is what you will have to upload to the Icy website to distribute the plugin (lower case only and no space character).



 Replace the following fields by values that will be informative to an end user:

```xml
<name>A template for Icy plugins.</name>
<description>This repo serves as a template for you to implement new Icy plugins.</description>
<url></url>
<inceptionYear>2020</inceptionYear>
```



The dependency section contains only the Icy kernel (that has all the application, GUI, etc...) and the EzPlug library for now. Add more if you need it. The version of the `icy-kernel` and `ezplug` are defined in the parent pom, so you don't have to specify them here.

```xml
<!-- List of project's dependencies -->
<dependencies>
  <!-- The core of Icy -->
  <dependency>
    <groupId>org.bioimageanalysis.icy</groupId>
    <artifactId>icy-kernel</artifactId>
  </dependency>

  <!-- The EzPlug library, simplifies writing UI for Icy plugins. -->
  <dependency>
    <groupId>org.bioimageanalysis.icy</groupId>
    <artifactId>ezplug</artifactId>
  </dependency>

</dependencies>
```



We need to specify where to find the dependency, so that last section has this paragraph:

```xml
<!-- Link to third-party Maven repositories -->
<repositories>
  <repository>
    <id>icy</id>
    <name>Icy's Nexus</name>
    <url>https://icy-nexus.pasteur.fr/repository/Icy/</url>
  </repository>
</repositories>
```

Just keep it as is.

### Package conventions in Icy.

By convention the Java package of Icy plugins takes the following form:

```java
package plugins.authorname.pluginname
```

`authorname` should be a condensed version of your name. For instance the author Alexandre Dufour uses `adufour`. Jean-Yves Tinevez uses `tinevez` _etc_.

`pluginname` should be a condensed name of the plugin you are currently developing, unique among all the plugins you develop to avoir package name clashes with the Icy application. 

For instance the classes of the demo you will find in this template repository are in the package `plugins.authorname.templateplugin`. 

## Examples and documentation.

There is a pdf presentation online that gives starters as how to build an Icy plugin in Java:

http://icy.bioimageanalysis.org/doc/icy-plugin.pdf

The two examples in this repository are inspired by it. You will find:

- A simple plugin example, that modify an image with no UI: [the first example](src/main/java/plugins/authorname/templateplugin/MyIcyPlugin.java)
- A second example plugin that uses [EzPlug](http://icy.bioimageanalysis.org/plugin/ezplug-sdk/) to build a basic UI for a plugin: [the second example](src/main/java/plugins/authorname/templateplugin/MyEzPlugIcyPlugin.java)

## Credits.

The example image found in the `samples` folder is courtesy  of Jakub Sedzinsky, University of Copenhagen. It is an epithelium of the neural plate of _Xenopus laevis_ visualized by utrophin-GFP.


## Installation instructions

For end-users, refer to the documentation on the Icy website on [how to install an Icy plugin](http://icy.bioimageanalysis.org/tutorial/how-to-install-an-icy-plugin/).      

For developers, see our [Contributing guidelines](https://gitlab.pasteur.fr/bia/icy/-/blob/master/CONTRIBUTING.md) and [Code of Conduct](https://gitlab.pasteur.fr/bia/icy/-/blob/master/CODE-OF-CONDUCT.md).


## Main functions and usage

<!-- list main functions, explain architecture, classname, give info on how to get started with the plugin. If applicable, how the package compares to other similar packages and/or how it relates to other packages -->

Classname: `plugins.authorname.MyIcyPlugin` or `plugins.authorname.MyEzPlugIcyPlugin`


## Citation 

Please cite this plugins as follows:          


Please also cite the Icy software and mention the version of Icy you used (bottom right corner of the GUI or first lines of the Output tab):     
de Chaumont, F. et al. (2012) Icy: an open bioimage informatics platform for extended reproducible research, [Nature Methods](https://www.nature.com/articles/nmeth.2075), 9, pp. 690-696       
http://icy.bioimageanalysis.org    



## Author(s)      

Amandine TOURNAY, Jean-Yves Tinevez, Stéphane Dallongeville


## Additional information
