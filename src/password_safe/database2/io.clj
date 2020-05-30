(ns password-safe.database2.io
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [taoensso.timbre :as timbre]))

(def db-path "db")
(def chunk-size 2)

(def collector (atom #{}))

(declare write-db-data)

(defn process-chunk [path chunk]
  (timbre/info "processing chunk:" chunk)
  (let [chunk-contents (for [file-id chunk]
                         (flatten (json/read-str (slurp (io/file path (str file-id ".json"))) :key-fn keyword)))]
    (timbre/info "chunk contents:" chunk-contents)
    (try
      (timbre/info "try to write" chunk "in chunk")
      (write-db-data path (flatten chunk-contents) (str "chunk-" (java.util.UUID/randomUUID)))
      (catch Exception e
        (timbre/error "Can not write" chunk "in chunk")
        (println e))))
  (doseq [file-id chunk]
    (timbre/info "deleting:" file-id)
    (io/delete-file (io/file path (str file-id ".json"))))
)

(defn watch-collector [path collector]
  (add-watch collector :watch-collector
             (fn [key atom old-state new-state]
               (when (> (count new-state) chunk-size)
                 (let [new-chunk (take chunk-size new-state)
                       rest (drop chunk-size new-state)]
                    (timbre/info "new-chunk:" new-chunk)
                    (timbre/info "rest of chunk:" rest)
                   (reset! collector rest)
                   (process-chunk path new-chunk))))))

(defn read-db [path]
  (flatten (for [f (file-seq (io/file path))
                 :when (not (.isDirectory f))]
             (map
              (fn [item]
                (let [id (:id item)]
                  (assoc item :id (java.util.UUID/fromString id))))
              (json/read-str (slurp f)
                             :key-fn keyword)))))

(defn write-db-data
  ([path coll]
   (write-db-data path coll (java.util.UUID/randomUUID)))
  ([path coll id]
   (println "coll: " coll)
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