(ns password-safe.database2.core
  (:require [password-safe.database2.io :as io]))

(def db (ref {}))

(defn create-db-entry [entry]
  (let [id (if (nil? (:id entry))
             (java.util.UUID/randomUUID)
             (:id entry))
        ts (System/currentTimeMillis)
        deleted (if (nil? (:deleted entry))
                  false
                  (:deleted entry))]
    [id (assoc entry :id id :ts ts :deleted deleted)]))

(defn init! []
  (let [current-state (io/receive-current-state io/db-path)]
    (dosync
     (alter db into current-state))))

(defn add! [entry]
  (let [[id db-entry] (create-db-entry entry)]
    (dosync
     (alter db assoc id db-entry)
     (io/write-db-dump io/db-path [db-entry]))))
