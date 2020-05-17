(ns password-safe.core
  (:require [password-safe.database.service :as db])
  (:gen-class))

(defn -main []
  (println "initializing db...")
  (db/init!))
