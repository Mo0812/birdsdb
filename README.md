![](https://github.com/Mo0812/birdsdb/workflows/Test/badge.svg)

# birdsdb

![doc/birdsdb_logo.png](doc/birdsdb_logo_boxed.jpg)

`birdsdb` is a database written in Clojure, which allows you to store and query objects. Instead of maintaining a single mutuable instance of the provided data, changes get stored as additional records seperated by timestamps so that every state of an object is reproducable and redoable.

While information gets stored persistently in the file system, a running instance of `birdsdb` is working with a selected copy of the database state in memory to improve performance.

Besides that `birdsdb` is build to be scalable. Multiple instances of a database can be run simultaneously on the same data basis while all of those instances keep their state in sync.

`birdsdb` can be accessed by a TCP connection, prompted directly in console, can be integrated in your own Clojure project, or at a later stage also be accessed by several clients.

A complete list of the features is listed below.

## Installation

For now, `birdsdb` is in a early development stage, so you can only clone and integrate it in your project. While the project is making more progress, this section gets updated.

> More information coming soon...

## Features

`birdsdb` is a simple database with the following features:

-   **storing objects:** The database can store and maintain any kind of hashmap in clojure. Internally those data objects are stored persistently as `edn` files in the file system.
-   **revisions & immutable data:** The database do not mutate any kind of stored object. This has clear advantages: Every change of a data object will result in a additional copy of it in stored in the database. Therefore any change of a data object can be revisioned at any state which once was maintained in the database. Also this technique allows to have a simpler approach of persistent data handling at the file level and allows the given approach of multiple instances (scale) with synced data foundations.
-   **in-memory:** While the _single source of truth_ of the stored information in the database consists of the maintained files in the file system, `birdsdb` handles queries during runtime with an additional in-memory part. This part is synced with the changes, both from data mutation in the own instance and the detected mutations of other instances when running in synced mode. Therefore querying not depends on the access rate of the file system, but is fastly delivered through holding also a copy of needed in formation in memory. Nevertheless it still can fallback to the stored information in the files at any time, due this part should only boost performance.
-   **several connection methods:** Once an instance of the database is running, it can easily accessed over TCP or a implemented CLI interface. With that communication methods given, client libraries based on those can be easily implemented on purpose when needed. When `birdsdb` is used as a database inside a clojure project, the whole database system can of course be accessed directly inside the project through clojure code without connecting to other available communication methods.
-   **syncing & scalability:** `birdsdb` works as a standalone database, started as a process and accessed over tcp or the given prompt (for now). Nevertheless also multiple instances of `birdsdb` relying on the same data foundation is possible. Therefore those instances can be run in _synced mode_ in which each database instance watches for changes in the filesystem and reacts with adding those to its in-memory database-state copy. Because of the immutable approach of stored objects, falsely repeatedly detected changes of the same data objects, or even older mutations get processed correctly, so that the state of each of the running instances is not in danger at any time.
-   **self-managed approach:** While during a mutation process of a data object, `birdsdb` directly write those changes to the file system, to prevent inconsistencies in the data foundation, those single mutations encapsulated in a single file will slow down the startup time of a database instance over time and also produce slidely more io operations as needed. Therefore the database tries to manage it self at several parts of the application. One of those self-managed processes is to summarize the changes written as single encapsulated mutations. By keeping track of the mutations and reacting at a predefined amount of their file representation, the self-managed process summarize those mutation in a file consisting that fixed amount of data objects in a so called _chunk_. As said before chunking enables a more pesistent and less resource consuming inital startup process of an `birdsdb` database instance.

![doc/blueprint_db.jpg](doc/blueprint_db.jpg)

## Usage

When `birdsdb` is compiled and started as a jar the defined base config in `project.clj` is used. Because the config is managed by [config](https://github.com/yogthos/config) the base config file can be overwritten with an external one at any time by providing it as an environment variable:

```
java -Dconfig="path/to/my/config.edn" -jar target/birdsdb.jar
```

In development the base config path can be changed in the `project.clj` declaration or the config file itselfs can be modifed to your own needs.

All information about available options and their meanings are listed in the _Options_ section.

## Options

To configure a `birdsdb` instance and enabling or disabling several features either a config file or the according command line parameters can be used.

When using a config file to configure an instance, the system relies on the [config](https://github.com/yogthos/config) package to manage several ways and layers of doing so.

The easiest way to configure an `birsdb` instance in development is to either change the existing config file under `env/dev/config.edn` or link a modified copy of that file and its path in the `project.clj`.
In production you can follow the instructions in the _Usage_ section.

In the following all available options to configure an instance with such a config file are listed below, side-by-side with the according command line parameters.

### Options via `config.edn`

| Category | Option         | Meaning | config parameter                    | default value |
| -------- | -------------- | ------- | ----------------------------------- | ------------- |
| logging  | enabled?       |         | `(-> env :logging :enabled?)`       | `false`       |
| logging  | print-logs?    |         | `(-> env :logging :print-logs?)`    | `false`       |
| logging  | write-logs?    |         | `(-> env :logging :write-logs)`     | `false`       |
| logging  | output-path    |         | `(-> env :logging :output-path)`    | `”db.log“`    |
| logging  | output-level   |         | `(-> env :logging :output-level)`   | `:debug`      |
| db       | db-path        |         | `(-> env :db :io :db-path)`         | `“db“`        |
| db       | chunk-enabled? |         | `(-> env :db :chunker :enabled?)`   | `false`       |
| db       | chunk-size     |         | `(-> env :db :chunker :chunk-size)` | `2`           |
| db       | sync-enabled?  |         | `(-> env :db :sync :enabled?)`      | `false`       |
| server   | port           |         | `(-> env :server :port)`            | `50937`       |

### Options via command line

| Option                  | CLI parameter   | Meaning | default value |
| ----------------------- | --------------- | ------- | ------------- |
| start integrated server | `-s` `--server` |         | `false`       |
| define server port      | `--port`        |         | `50937`       |
| start in prompt mode    | `-p` `--prompt` |         | `false`       |

Eaxmple call to start a `birsdb` instance with certain cli parameters:

```
java -Dconfig="path/to/my/config.edn" -jar target/birdsdb.jar --server --port 12345
```

## Examples

... this section will be completed soon ...

## Roadmap

-   [x] edn-based chunk watcher
-   [x] fully integrate sync-mode over file based changes
-   [ ] time travel to former states of stored information
-   [ ] use of advanced caching strategies to optimize the in-memory database part
-   [ ] sync-mode based on inter-process communication
-   [ ] finishing redundancy feature
-   [ ] possibility to scale up instances by intelligent chunking of contents across multiple instances
-   [ ] create query syntax for accessing data from prompt and server interface
-   [ ] performance testing
-   [ ] developing testing further
-   [ ] ~~encryption based on end-to-end communication and implementing decent user right management thereby~~

## Bugs

If you have any ideas, suggestions for improvements or misbehavior, plan to contribute or just find any kind of bug in `birdsdb` please file an issue and discuss topic-base timely.

All in all I am lucky for every reaction regarding this database project.

## Contribution

We are very lucky about every one who is willing to contribute to this project. No matter what it is about extending, enhancing or critizing certain parts and overall helps to make `birdsdb` more stable, reliable and production ready. The ultimate goal could be to build a database together, which then will be used in real projects of all kinds.
Even if you only detect some issues, misconceptions or even typos, feel free to create a pull request or raise an issue.

A `CONTRIBUTING.md` will may follow later on in this repo.

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
