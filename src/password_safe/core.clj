(ns password-safe.core
  (:require [password-safe.database.service :as service])
  (:require [password-safe.interface.core :as interface])
  (:gen-class))

(defn -main [& args]
  (println "args are: " args)
  (service/init!)
  (interface/execute args))
