# robocup-instrumentation

[![Maven Central](https://img.shields.io/maven-central/v/com.github.toromtomtom/robocup-instrumentation.svg)](https://search.maven.org/#artifactdetails%7Ccom.github.toromtomtom%7Crobocup-instrumentation%7C0.1%7Cjar)
[![Build Status](https://travis-ci.org/Toromtomtom/robocup-instrumentation.svg?branch=master)](https://travis-ci.org/Toromtomtom/robocup-instrumentation)

## Synopsis

This project contains code to start and observe an instance of the robocup soccer server (https://sourceforge.net/projects/sserver/). 

## Use it

To use robocup-instrumentation in your Maven project, add it as a dependency:

~~~~
<dependency>
   <groupId>com.github.toromtomtom</groupId>
   <artifactId>robocup-instrumentation</artifactId>
   <version>0.1</version>
</dependency>
~~~~

## Process

1. Start instances of the soccer server and two teams.
2. Register as an observer of the server. The observer receives the positions of all players and the ball at each step and forwards these information to a message handler. Two simple message handler implementations are provided.    
3. Wait for the match to finish.
  
## Contributors

* [Tom Warnke](https://github.com/Toromtomtom) (<tom.warnke@acm.org>)

## License

This project is distributed under the terms of the Apache 2.0 license.

## Dependencies/Acknowledgements

* ANTLR (https://github.com/antlr/antlr4)
* Atan (https://github.com/robocup-atan/atan)
