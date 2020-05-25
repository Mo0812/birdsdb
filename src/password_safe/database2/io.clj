(ns password-safe.database2.io
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]))

(def db-path "db")
(def chunk-size 2)

(def collector (atom #{}))

(declare write-db-data)

(defn process-chunk [path chunk]
  (let [chunk-contents (for [file-id chunk]
                         (flatten (json/read-str (slurp (io/file path (str file-id ".json"))))))]
    (try
      (write-db-data path (flatten chunk-contents) (str "chunk-" (java.util.UUID/randomUUID)))
      (for [file-id chunk]
        (io/delete-file (io/file path (str file-id ".json"))))
      (catch Exception e
        (println e)))))

(defn watch-collector [path collector]
  (add-watch collector :watch-collector
             (fn [key atom old-state new-state]
               (when (> (count new-state) chunk-size)
                 (let [new-chunk (take chunk-size new-state)
                       rest (drop chunk-size new-state)]
                   (reset! collector rest)
                   (process-chunk path new-chunk))))))

(defn read-db [path]
  (flatten (for [f (file-seq (io/file path))
                 :when (not (.isDirectory f))]
             (map
              (fn [item]
                (let [id (:id item)]
                  (println id)
                  (assoc item :id (java.util.UUID/fromString id))))
              (json/read-str (slurp f)
                             :key-fn keyword)))))

(defn write-db-data
  ([path coll]
   (write-db-data path coll (java.util.UUID/randomUUID)))
  ([path coll id]
   (spit (io/file path (str id ".json"))
         (json/write-str
          (map #(update % :id str) coll)))))

(defn save [path coll]
  (let [file-id (java.util.UUID/randomUUID)]
    (.start (Thread. (fn []
                       (try
                         (write-db-data path coll file-id)
                         (swap! collector conj file-id)
                         (catch Exception e
                           (println "Error occured: " e))))))))

(defn receive-all [path]
  (group-by :id (read-db path)))

(defn receive-current-state [path]
  (into {} (for [[id revisions] (receive-all path)
                 :let [current-entry (last (sort-by :ts revisions))]
                 :when (not (= (:deleted current-entry) true))]
             [id current-entry])))