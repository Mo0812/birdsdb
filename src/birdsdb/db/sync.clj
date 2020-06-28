(ns birdsdb.db.sync
  (:require [juxt.dirwatch :refer [watch-dir close-watcher]]
            [birdsdb.db.io :as io]
            [birdsdb.db.db :as db]
            [birdsdb.logger.logger :as log]
            [config.core :refer [env]]))

(def sync-state (atom nil))

(declare stop)

(defn sync-file-state [file-obj]
  (log/log :debug "file change detected" file-obj)
  (when (and
         (not (contains? @io/protocol (io/decompose-filename (:file file-obj))))
         (= (:action file-obj) :create))
    (let [entries (io/read-file (:file file-obj))]
      (doseq [entry entries]
        (log/log :info "processing entry detected by sync watcher" entry)
        (db/add! entry {:io false})))))

(defn generate-sync-watcher [db-path]
  (log/log :info "generating new sync watcher")
  (watch-dir sync-file-state (clojure.java.io/file db-path)))

(defn start [db-path]
  (when (-> env
            :db
            :sync
            :enabled?)
    (log/log :info "restarting sync watcher")
    (stop)
    (reset! sync-state (generate-sync-watcher db-path))))

(defn stop []
  (when (not (nil? @sync-state))
    (log/log :info "stopping sync watcher")
    (close-watcher @sync-state))
  (reset! sync-state nil))