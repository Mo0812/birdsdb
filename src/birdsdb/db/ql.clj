(ns birdsdb.db.ql
  (:require [birdsdb.db.db :as db]
            [birdsdb.db.filter :as filter]))

(defn insert [item]
  (db/add! {:object item}))

(defn update [id item]
  (db/add! {:id id :object item}))

(defn delete [id]
  (db/add! {:id id :deleted true}))

(defn parse-db-entry [[id db-entry]]
  [id (:object db-entry)])

(defn select
  ([& xfs]
   (into {} (sequence (apply comp xfs) (into {} (map parse-db-entry @db/db))))))