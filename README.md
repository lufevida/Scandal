# Scandal

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Scandal is both a Java framework and a domain-specific language designed to process sounds. Examples of how to use the language, as well as a complete description of its [syntax](https://github.com/lufevida/Scandal/blob/master/src/language/sublime/Syntax.md) can be found in the [sublime](https://github.com/lufevida/Scandal/blob/master/src/language/sublime) package. Examples of how to use the framework can be found in the [examples](https://github.com/lufevida/Scandal/blob/master/src/framework/examples) package.

## Installation

The very first step is to have [Java](https://www.java.com/en/download/) installed. Then go to the [releases](https://github.com/lufevida/Scandal/releases) page and download the `Scandal.zip` file. Next, open [Sublime Text](https://www.sublimetext.com), and select
```
Sublime Text -> Preferences -> Browse Packages...
```
Drop the decompressed `Scandal` folder in there. To build your programs, use `command + b`. To cancel execution, hit `control + c`. To use the framework alone, import the `Scandal.jar` file using your preferred IDE.

## Acknowledgments

Scandal is being developed by [Luis F. Vieira Damiani](http://vieira-damiani.com) under the orientation of [Dr. Beverly Sanders](https://www.cise.ufl.edu/people/faculty/sanders). Scandal utilizes the [ASM](http://asm.ow2.org) framework for compiling its domain-specific language.