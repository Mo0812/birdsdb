(ns birdsdb.database.service
  (:require [birdsdb.database.core :as db]
            [birdsdb.database.query :as query]))

(defn init! []
  (println "init db...")
  (db/init! db/db (db/read-db-dump db/db-path)))

(defn query [needle]
  (query/full-search @db/db needle))