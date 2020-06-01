(ns password-safe.database2.ql
  (:require [password-safe.database2.db :as db]))

(defn insert [item]
  (db/add! {:object item}))

(defn update [id item]
  (db/add! {:id id :object item}))

(defn delete [id]
  (db/add! {:id id :deleted true}))

(defn select []
  @db/db)