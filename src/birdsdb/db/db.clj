(ns birdsdb.db.db
  (:require [birdsdb.logger.logger :as log]
            [birdsdb.db.io :as io]))

(def db (ref {}))

(defn create-db-entry [entry]
  (let [id (if (nil? (:id entry))
             (java.util.UUID/randomUUID)
             (:id entry))
        ts (if (nil? (:ts entry))
             (System/currentTimeMillis)
             (:ts entry))
        deleted (if (nil? (:deleted entry))
                  false
                  (:deleted entry))
        object (:object entry)]
    [id (assoc {} :id id :ts ts :deleted deleted :object object)]))

(defn init! []
  (let [current-state (io/receive-current-state io/db-path)]
    (dosync
     (alter db into (for [[id db-entry] current-state]
                      [id (:object db-entry)])))))

(defn add! [entry]
  (let [[id db-entry] (create-db-entry entry)
        deleted (:deleted db-entry)]
    (.start (Thread. (fn []
                       (try
                         (io/save io/db-path [db-entry])
                         (dosync (if deleted
                                   (alter db dissoc db id)
                                   (alter db assoc id (:object db-entry))))
                         (catch Exception e
                           (log/log :error e))))))))
