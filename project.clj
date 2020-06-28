(defproject birdsdb "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/data.json "1.0.0"]
                 [org.clojure/test.check "1.0.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [yogthos/config "1.1.7"]
                 [juxt/dirwatch "0.2.5"]
                 [org.clojure/tools.cli "1.0.194"]
                 [criterium "0.4.5"]
                 [clansi "1.0.0"]]
  :main ^:skip-aot birdsdb.core
  :repl-options {:init-ns birdsdb.core
                 :init (-main)}
  :target-path "target/%s"
  :jvm-options ["Xms128m" "-Xmx2g"]
  :profiles {:uberjar {:aot :all}
             :dev {:resource-paths ["env/dev/config"]}
             :test {:resource-paths ["env/test/config"]}})
