# Röckdöt IntelliJ Plugin - Create new Dart+StageXL+Röckdöt projects with ease!

## About

The purpose of this Plugin for IntelliJ is to take care of ALL THE THINGS required to start with development of Röckdöt projects right away.

The Röckdot family of libraries for Dart give you 

* pixel-precise control over the html CanvasElement (2D and WebGL) through StageXL, the Flash DisplayList API for HTML5, written in Dart
* a blazing fast, industry proven IoC/DI/MVC+Command framework based on Spring
* smart UI lifecycle management
* a codebase that has its roots in Pure ActionScript, and has been in continuous development for about six years (2008 - 2014),
* a codebase that has served millions of pageviews in individual web apps, microsites and specials for brands such as Mercedes-Benz, Nike, and Nikon.

With Röckdöt, you write apps as well as games (or a mix of the two) in Pure Dart. No HTML, no CSS.

[Demo](http://rockdot.sounddesignz.com/template/).

### Building Blocks
* [Röckdöt Framework](https://github.com/blockforest/rockdot-framework) Plugin System, UI Lifecycle and Asset Manager, i18n, Google and Facebook Integration, Generic User Generated Content backend communication
* [Röckdöt Spring](https://github.com/blockforest/rockdot-spring) IoC/DI container (ObjectFactory, ObjectFactory and Object Postprocessing, Interface Injection)
* [Röckdöt Commons](https://github.com/blockforest/rockdot-commons) Async library (FrontController and Commands/Operations, also sequences)
* [Röckdöt Commons](https://github.com/blockforest/rockdot-commons) Material Design Implementation
* [Röckdöt Commons](https://github.com/blockforest/rockdot-commons) EventBus (with some tweaks to Operations to make them as effective as Signals)
* [Röckdöt Commons](https://github.com/blockforest/rockdot-commons) Logging
* [StageXL](https://github.com/bp74/StageXL) - Flash API for Dart

## Installation

I have yet to publish this to an IntelliJ plugin repository, but here are instructions how to build and debug this plugin:

### Requirements

* Dart SDK 1.13 or greater on your path
* use newest (EAP if possible) intellij IDEA ultimate
* Run and Install Dart Plugin
* Clone and open this project


Under Project Structure, check or create:

#### A
SDK "IDEA Rockdot" (type IntelliJ Platform SDK), pointing to /Applications/IntelliJ IDEA 2016.2 CE EAP.app/Contents
- Add to classpath all jars from /Users/ndoehring/Library/Application Support/IdeaIC2016.2/Dart/lib
- Add to classpath JavaScriptDebugger.jar from /Applications/IntelliJ IDEA 2016.2 CE EAP.app/Contents/plugins/JavaScriptDebugger/lib

(These are the locations on my current Mac OSX. Yours could vary.)

#### B
Library "Dart"
- add /Users/ndoehring/Library/Application Support/IdeaIC2016.2/Dart/lib/Dart.jar
- add /Users/ndoehring/Library/Application Support/IdeaIC2016.2/Dart/lib/json.jar
(These are the locations on my current Mac OSX. Yours could vary.)


#### C
Module "rockdot-intellij"
- under dependencies, select the sdk created in step A
- add dependency to /Applications/IntelliJ IDEA 2016.2 CE EAP.app/Contents/plugins
- add library from step B

#### D
Copy Dart.jar
from /Users/ndoehring/Library/Application Support/IdeaIC2016.2/Dart/lib/ to /Users/ndoehring/Library/Caches/IdeaIC2016.2/plugins-sandbox

### Debug

    # Run or Debug as Plugin via IntelliJ

# Usage