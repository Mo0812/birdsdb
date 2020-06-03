(ns birdsdb.database2.io
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [birdsdb.logger.logger :as log]
            [config.core :refer [env]]))

(def db-path (-> env
                 :db
                 :io
                 :db-path))

(def chunk-size (-> env
                    :db
                    :io
                    :chunk-size))

(def collector (atom #{}))

(declare write-db-data read-file)

(defn process-chunk [path chunk]
  (log/log :info "processing chunk:" (pr-str chunk))
  (let [chunk-contents (for [file-id chunk]
                         (flatten (json/read-str (slurp (io/file path (str file-id ".json"))) :key-fn keyword)))]
    (log/log :info "chunk contents:" (pr-str chunk-contents))
    (try
      (log/log :info "try to write" (pr-str chunk) "in chunk")
      (write-db-data path (flatten chunk-contents) (str "chunk-" (java.util.UUID/randomUUID)))
      (doseq [file-id chunk]
        (log/log :info "deleting:" file-id)
        (io/delete-file (io/file path (str file-id ".json"))))
      (catch java.io.FileNotFoundException e
        (swap! collector #(apply conj % chunk))
        (log/log :error e))
      (catch Exception e
        (log/log :error "Can not write" (pr-str chunk) "in chunk")
        (log/log :error e)))))

(defn watch-collector [path]
  (add-watch collector :watch-collector
             (fn [key atom old-state new-state]
               (when (> (count new-state) chunk-size)
                 (let [new-chunk (take chunk-size new-state)
                       rest (drop chunk-size new-state)]
                   (log/log :info "new-chunk:" (pr-str new-chunk))
                   (log/log :info "rest of chunk:" (pr-str rest))
                   (reset! collector rest)
                   (.start (Thread. (fn []
                                      (process-chunk path new-chunk)))))))))

(defn read-db [path]
  (flatten (for [f (file-seq (io/file path))
                 :when (not (.isDirectory f))]
             (read-file f))))

(defn read-file [file]
  (map
   (fn [item]
     (let [id (:id item)]
       (assoc item :id (java.util.UUID/fromString id))))
   (json/read-str (slurp file)
                  :key-fn keyword)))

(defn write-db-data
  ([path coll]
   (write-db-data path coll (java.util.UUID/randomUUID)))
  ([path coll id]
   (spit (io/file path (str id ".json"))
         (json/write-str
          (map #(update % :id str) coll)))))

(defn save [path coll]
  (let [file-id (java.util.UUID/randomUUID)]
    (try
      (write-db-data path coll file-id)
      (swap! collector conj file-id)
      (catch Exception e
        (log/log :error e)
        (throw e)))))

(defn receive-all [path]
  (group-by :id (read-db path)))

(defn receive-current-state [path]
  (into {} (for [[id revisions] (receive-all path)
                 :let [current-entry (last (sort-by :ts revisions))]
                 :when (not (= (:deleted current-entry) true))]
             [id current-entry])))