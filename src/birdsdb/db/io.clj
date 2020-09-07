(ns birdsdb.db.io
  (:require [clojure.java.io :as io]
            [birdsdb.logger.logger :as log]
            [birdsdb.db.specs :as bdb-specs]
            [clojure.spec.alpha :as s]))

(def protocol (atom #{}))

(defn compose-filename [id]
  (str id ".edn"))

(defn decompose-filename [file]
  (clojure.string/replace (.getName file) #"[.][^.]+$" ""))

(defn read-file [file]
  (let [file (io/file file)
        file-dump (read-string (slurp file))]
    (when (s/valid? ::bdb-specs/file-dump file-dump)
      (log/log :info "reading:" file)
      (swap! protocol conj (decompose-filename file))
      file-dump)))

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

(defn receive-specific-state [ts path]
  (into {} (for [[id revisions] (receive-all path)
                 :let [last-matching-entry ((sort-by :ts revisions))]
                 :when (not (= (:deleted current-entry) true))]
             [id current-entry])))

(defn receive-current-state [path]
  (into {} (for [[id revisions] (receive-all path)
                 :let [current-entry (last (sort-by :ts revisions))]
                 :when (not (= (:deleted current-entry) true))]
             [id current-entry])))