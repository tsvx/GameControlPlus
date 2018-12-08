# To build the library

1. Install `jdk-8u191-windows-x64.exe`.  
   Add `C:\Program Files\Java\jdk1.8.0_191\bin` to your `PATH` environment variable.
2. Download `apache-ant-1.10.5-bin.zip`, unpack to `C:\bin\apache-ant`.
3. Download `processing-3.4-windows64.zip`, unpack to `D:\Tema\Fedor\processing-3.4`.
4. Clone the repository of the fork, note the [commit setting up the build process](0ff06a27).
5. Run `build.cmd` from directory `gamecontroller-code\Game Control Devices\resources\`.

There should be a message `BUILD SUCCESSFUL` in the end of the build process.

Then take the jar from `gamecontroller-code\Game Control Devices\distribution\GameControlPlus-6\download\GameControlPlus.zip`.