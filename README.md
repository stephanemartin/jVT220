# jVT220 - Java VT220 terminal emulator

This repository is forked from [jvt220](https://github.com/jawi/jVT220).
One of the most important changes in this forked version, is the addition of a [TerminalClient](/src/main/java/nl/lxtreme/jvt220/terminal/TerminalClient.java)  class, which allows to actually connect to a VT-type mainframe application, and then using this well modularized project to parse the received information from [TelnetClient](https://commons.apache.org/proper/commons-net/) socket. 

For more information about the original project please visit the documentation [here](https://github.com/jawi/jVT220/blob/master/README.md) 

## Usage 

To use the emulator as Maven dependency include in `pom.xml`:

```xml
  <dependency>
      <groupId>com.github.stephanemartin</groupId>
 	  <artifactId>jVT220</artifactId>
  	  <version>jvt220-v1.3.3-SNAPSHOT</version>
  </dependency>
```

>Check latest version in[releases](https://github.com/stephanemartin/jVT220/releases).

## Author

It is originally written by J.W. Janssen, <j.w.janssen@lxtreme.nl>.

This version is currently maintained by tricentis. 

## License

The code in this library is licensed under Apache Software License, version 
2.0 and can be found online at: <http://www.apache.org/licenses/LICENSE-2.0>.

