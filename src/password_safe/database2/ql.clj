(ns password-safe.database2.ql
  (:require [password-safe.database2.core :as db]))

(defn insert [item]
  (db/add! item))

(defn update [item]
  (db/add! item))

(defn delete [item]
  (db/add! (assoc item :deleted true)))

(defn select [])