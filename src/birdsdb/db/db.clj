(ns birdsdb.db.db
  (:require [clojure.spec.alpha :as s]
            [birdsdb.logger.logger :as log]
            [birdsdb.db.io :as io]
            [config.core :refer [env]]))

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

(defn init!
  [db-path]
  (let [current-state (io/receive-current-state db-path)]
    (dosync
     (log/log :info "resetting in memory db")
     (ref-set db {})
     (log/log :info "parsing existing data to in memory db")
     (alter db into (for [[id db-entry] current-state]
                      [id db-entry])))))

(defn add!
  ([entry]
   (add! entry {:io true :db-path (-> env
                                      :db
                                      :io
                                      :db-path)}))
  ([entry options]
   (let [[id db-entry] (create-db-entry entry)
         deleted (:deleted db-entry)]
     (future (try
               (when (-> options
                         :io)
                 (io/save (-> options
                              :db-path) [db-entry]))
               (log/log :info "processing db-entry to in memory db")
               (dosync (if deleted
                         (alter db dissoc db id)
                         (alter db assoc id db-entry)))
               (catch Exception e
                 (log/log :error e)))))))

(defn time-travel! [ts]
  (println "traveling to" ts))