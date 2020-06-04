# birdsdb

`birdsdb` is a database written in Clojure, which allows you to store and query objects. Instead of maintaining a single mutuable instance of the provided data, changes get stored as additional records seperated by timestamps so that every state of an object is reproducable and redoable.

While information gets stored persistently in the file system, a running instance of `birdsdb` is working with a selected copy of the database state in memory to improve performance.

Besides that `birdsdb` is build to be scalable. Multiple instances of a database can be run simultaneously on the same data basis while all of those instances keep their state in sync.

`birdsdb` can be accessed by a TCP connection, prompted directly in console, can be integrated in your own Clojure project, or at a later stage also be accessed by several clients.

A complete list of the features can is listed below.

## Installation

Download from http://example.com/FIXME.

## Features 

...

## Usage

FIXME: explanation

    $ java -jar birdsdb-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## Examples

...

## Roadmap

...

## Bugs

...

## License

Copyright © 2020 Moritz Kanzler

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
