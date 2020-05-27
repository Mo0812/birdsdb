(ns password-safe.core
  (:require [password-safe.database2.core :as db])
  (:gen-class))

(defn -main [& args]
  (println "args are: " args)
  ;; (service/init!)
  ;; (interface/execute args)
)
