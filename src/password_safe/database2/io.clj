(ns password-safe.database2.io
  (:require [clojure.data.json :as json]))

(def db-path "db.json")

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

(defn receive-all [file]
  (group-by :id (read-db-dump file)))

(defn receive-current-state [file]
  (for [[id entry-group] (receive-all file)]
    (last (sort-by :ts entry-group))))