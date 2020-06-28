(ns birdsdb.db.service
  (:require [birdsdb.db.db :as db]
            [birdsdb.db.chunker :as chunker]
            [birdsdb.db.sync :as sync]
            [config.core :refer [env]]))

(defn start ([]
             start (-> env
                       :db
                       :io
                       :db-path))
  ([db-path]
   (db/init! db-path)
   (chunker/watch-collector db-path)
   (sync/start db-path)))