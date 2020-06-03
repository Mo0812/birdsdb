(ns birdsdb.database.core
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

(defn generate-id [item]
  (let [id (java.util.UUID/randomUUID)]
    (assoc item :id id)))

(defn get-index-by-id [coll id]
  (ffirst
   (filter
    (fn [[index val]]
      (= (:id val) id))
    (map-indexed vector coll))))

(defn init! [db data]
  (doseq [item data]
    (send-off db conj item))
  (watch-db db))

(defn add! [db item]
  (send db conj (generate-id item)))

(defn update! [db id new-item]
  (let [index (get-index-by-id @db id)]
    (send db update (int index) merge new-item)))


