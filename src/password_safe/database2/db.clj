(ns password-safe.database2.db
  (:require [clojure.data.json :as json]))

(def db-path "db.json")

(def db (agent []))

(defn read-db-dump [file]
  (map
   (fn [item]
     (let [id (:id item)]
       (assoc item :id (java.util.UUID/fromString id))))
   (json/read-str (slurp file)
                  :key-fn keyword)))

(defn write-db-dump [file state]
  (spit file
        (json/write-str
         (map #(update % :id str) state))))

(defn watch-db [db]
  (add-watch db :watcher (fn [key ref old-state new-state]
                           (write-db-dump db-path new-state))))

(defn create-db-entry [item]
  (let [id (if (nil? (:id item))
             (java.util.UUID/randomUUID)
             (:id item))
        ts (System/currentTimeMillis)]
    (assoc item :id id :ts ts)))

(defn init! [db data]
  (doseq [item data]
    (send-off db conj item))
  (await db)
  (watch-db db))

(defn add! [db item]
  (send db conj (create-db-entry item)))

(defn get [coll id]
  (first (filter #(= (:id %) id) coll)))
