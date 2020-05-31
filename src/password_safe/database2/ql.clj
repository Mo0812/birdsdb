(ns password-safe.database2.ql
  (:require [password-safe.database2.core :as db]))

(defn insert [item]
  (db/add! item))

(defn update [id item]
  (db/add! (assoc item :id id)))

(defn delete [id item]
  (db/add! (assoc item :id id :deleted true)))

(defn select []
  @db/db)