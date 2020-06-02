(ns birdsdb.database2.ql
  (:require [birdsdb.database2.db :as db]))

(defn insert [item]
  (db/add! {:object item}))

(defn update [id item]
  (db/add! {:id id :object item}))

(defn delete [id]
  (db/add! {:id id :deleted true}))

(defn select []
  @db/db)