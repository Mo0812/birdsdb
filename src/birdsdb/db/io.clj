(ns birdsdb.db.io
  (:require [clojure.java.io :as io]
            [birdsdb.logger.logger :as log]
            [config.core :refer [env]]))

(def db-path (-> env
                 :db
                 :io
                 :db-path))

(def protocol (atom #{}))

(defn compose-filename [id]
  (str id ".edn"))

(defn decompose-filename [file]
  (clojure.string/replace (.getName file) #"[.][^.]+$" ""))

(defn read-file [file]
  (log/log :info "reading:" file)
  (swap! protocol conj (decompose-filename file))
  (read-string (slurp file)))

(defn read-db [path]
  (flatten (for [f (file-seq (io/file path))
                 :when (not (.isDirectory f))]
             (read-file f))))

(defn write-db-data
  ([path coll]
   (write-db-data path coll (java.util.UUID/randomUUID)))
  ([path coll id]
   (log/log :info "writing:" path coll id)
   (spit (io/file path (compose-filename id))
         (pr-str coll))))

(defn save [path coll]
  (let [file-id (java.util.UUID/randomUUID)]
    (try
      (write-db-data path coll file-id)
      (swap! protocol conj (str file-id))
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