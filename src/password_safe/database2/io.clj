(ns password-safe.database2.io
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]))

(def db-path "db")

(defn read-db-dump [path]
  (flatten (for [f (file-seq (io/file path))
                 :when (not (.isDirectory f))]
             (map
              (fn [item]
                (let [id (:id item)]
                  (assoc item :id (java.util.UUID/fromString id))))
              (json/read-str (slurp f)
                             :key-fn keyword)))))

(defn write-db-dump [path state]
  (spit (io/file path (str (java.util.UUID/randomUUID) ".json"))
        (json/write-str
         (map #(update % :id str) state))))

(defn receive-all [path]
  (group-by :id (read-db-dump path)))

(defn receive-current-state [path]
  (into {} (for [[id revisions] (receive-all path)
                 :let [current-entry (last (sort-by :ts revisions))]
                 :when (not (= (:deleted current-entry) true))]
             [id current-entry])))