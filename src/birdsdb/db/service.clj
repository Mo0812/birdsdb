(ns birdsdb.db.service
  (:require [birdsdb.db.db :as db]))

(defn start []
  (db/init!))