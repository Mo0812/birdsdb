(ns password-safe.database2.service
  (:require [password-safe.database2.db :as db]))

(defn start []
  (db/init!))