(ns password-safe.database.service
  (:require [password-safe.database.core :as db]
            [password-safe.database.query :as query]))

(defn init! []
  (db/init! db/db (db/read-db-dump db/db-path)))

(defn query [needle]
  (query/full-search @db/db needle))