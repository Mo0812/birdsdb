(ns password-safe.database2.core
  (:require [password-safe.database2.io :as io]))

(def db (ref {}))

(defn watch-db []
  (add-watch db :watcher (fn [key ref old-state new-state]
                           (println new-state))))

(defn create-db-entry [entry]
  (let [id (if (nil? (:id entry))
             (java.util.UUID/randomUUID)
             (:id entry))
        ts (System/currentTimeMillis)]
    (assoc entry :id id :ts ts)))

(defn init! []
  (let [current-state (io/receive-current-state io/db-path)]
    (for [entry current-state]
      (dosync
       (alter db assoc (:id entry) entry)))))

(defn add! [entry]
  (let [db-entry (create-db-entry entry)]
    (dosync
     (alter db assoc (:id db-entry) db-entry))))
