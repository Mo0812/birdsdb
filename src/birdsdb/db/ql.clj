(ns birdsdb.db.ql
  (:require [birdsdb.db.db :as db]
            [birdsdb.db.filter :as filter]))

(defn insert-db
  ([item]
   (db/add! {:object item}))
  ([item options]
   (db/add! {:object item} options)))

(defn update-db [id item]
  (db/add! {:id id :object item}))

(defn delete-db [id]
  (db/add! {:id id :deleted true}))

(defn parse-db-entry [[id db-entry]]
  [id (:object db-entry)])

(defn select-db
  ([& xfs]
   (into {} (sequence (apply comp xfs) (into {} (map parse-db-entry @db/db))))))