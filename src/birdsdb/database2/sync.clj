(ns birdsdb.database2.sync
  (:require [juxt.dirwatch :refer [watch-dir close-watcher]]
            [birdsdb.database2.io :as io]
            [birdsdb.database2.db :as db]))

(def sync-state (atom nil))

(defn sync [file-obj]
  (doseq [entry (io/read-file (:file file-obj))]
    (println file-obj entry)
    (db/add! entry)))

(defn generate-sync-watcher []
  (watch-dir sync (clojure.java.io/file "db")))

(defn start []
  (reset! sync-state (generate-sync-watcher)))

(defn stop []
  (close-watcher @sync-state)
  (reset! sync-state nil))