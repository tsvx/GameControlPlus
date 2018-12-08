# The difference from the original library Game Control Plus

This fork is aimed to fix handling of CP1251 device names and properties.
More information [inside](FixCP1251.md).

Этот форк исправляет проблемы с кодировкой имён устройств и их свойств в библиотеке Game Control Plus.

[A commit](0ff06a27) has been added to facilitate build process on my computer,
[see here](Build.md) of what was installed and changed.

This fork is based on the
[SourceForge git repository of the original library](https://sourceforge.net/p/gamecontroller/code/ci/master/tree/)
plus [a commit](8658c754) to update the sources to v1.2.1, taken from
[the latest release on SF](https://sourceforge.net/projects/gamecontroller/files/GameControlPlus%20V1.2.1.zip/download).

Below is the original text.

----------

# Game Control Plus

Create sketches that use joysticks, gamepads etc. that can be easily configured to work on different platforms and with different control devices without having to change the source code. 
Refer to the [official website](http://lagers.org.uk/gamecontrol/index.html) for more information.

## Build Requirements

*   JDK
*   Processing
*   Apache Ant

## Build Process

1.  Open resources/build.properties in your favorite text editor.
2.  Modify the following variables to reflect your environment:
    *   sketchbook.location
    *   classpath.local.location
    *   classpath.local.include
    *   classpath.libraries.location
3.  Run the command `ant` in the resources directory.
