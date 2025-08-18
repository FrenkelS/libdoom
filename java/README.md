# libdoom driver Java version
With the Java Foreign Function & Memory API it's possible to call C code from Java and to let C code call Java.

Why would you want that? To run Doom, of course!

## How to run
1. Compile [libdoom](.), Doom as a dynamic library. Or [download](https://github.com/FrenkelS/libdoom/releases) the latest release.
2. Place `doom1.wad` in the same directory as `LibDoomDriver.java`.
3. Install Java 24.
4. Run `java LibDoomDriver.java`

## Controls
The usual, plus WASD to move and E to use.

## Command line arguments
|Command line argument|Effect                          |
|---------------------|--------------------------------|
|-2                   |Double    the size of the window|
|-3                   |Triple    the size of the window|
|-4                   |Quadruple the size of the window|
|-nomouse             |Disable mouse support           |
