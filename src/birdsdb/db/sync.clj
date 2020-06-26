(ns birdsdb.db.sync
  (:require [juxt.dirwatch :refer [watch-dir close-watcher]]
            [birdsdb.db.io :as io]
            [birdsdb.db.db :as db]
            [birdsdb.logger.logger :as log]))

(def sync-state (atom nil))

(declare stop)

(defn sync-file-state [file-obj]
  (println file-obj)
  (when (and (not (contains? io/protocol (io/decompose-filename (:file file-obj)))) (= (:action file-obj) :create))
    (let [entry (io/read-file (:file file-obj))]
      (println file-obj entry)
      (db/add! entry  {:io false}))))

(defn generate-sync-watcher []
  (log/log :info "generating new sync watcher")
  (watch-dir sync-file-state (clojure.java.io/file "db")))

(defn start []
  (log/log :info "restarting sync watcher")
  (stop)
  (reset! sync-state (generate-sync-watcher)))

(defn stop []
  (when (not (nil? @sync-state))
    (log/log :info "stopping sync watcher")
    (close-watcher @sync-state))
  (reset! sync-state nil))