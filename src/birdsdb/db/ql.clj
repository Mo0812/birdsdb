(ns birdsdb.db.ql
  (:require [birdsdb.db.db :as db]
            [birdsdb.db.filter :as filter]))

(defn insert [item]
  (db/add! {:object item}))

(defn update [id item]
  (db/add! {:id id :object item}))

(defn delete [id]
  (db/add! {:id id :deleted true}))

(defn select
  ([& xfs]
   (into {} (sequence (apply comp xfs) @db/db))))