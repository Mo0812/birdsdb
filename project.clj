(defproject password-safe "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/data.json "1.0.0"]
                 [org.clojure/test.check "1.0.0"]]
  :main ^:skip-aot password-safe.core
  :repl-options {:init-ns password-safe.core
                 :init (-main)}
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
