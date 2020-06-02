(ns birdsdb.database2.service
  (:require [birdsdb.database2.db :as db]))

(defn start []
  (db/init!))