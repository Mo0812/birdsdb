(ns birdsdb.db.chunker
  (:require [birdsdb.db.io :as bdio]
            [birdsdb.logger.logger :as log]
            [clojure.java.io :as io]
            [config.core :refer [env]]))

(def chunking-enabled? (-> env
                           :db
                           :chunker
                           :enabled?))

(def chunk-size (-> env
                    :db
                    :chunker
                    :chunk-size))

(defn process-chunk [path chunk]
  (log/log :info "processing chunk:" (pr-str chunk))
  (let [chunk-contents (for [file-id chunk]
                         (flatten (read-string (slurp (io/file path (bdio/compose-filename file-id))))))]
    (log/log :info "chunk contents:" (pr-str chunk-contents))
    (try
      (log/log :info "try to write" (pr-str chunk) "in chunk")
      (bdio/write-db-data path (flatten chunk-contents) (str "chunk-" (java.util.UUID/randomUUID)))
      (doseq [file-id chunk]
        (log/log :info "deleting:" file-id)
        (io/delete-file (io/file path (bdio/compose-filename file-id))))
      (catch java.io.FileNotFoundException e
        (swap! bdio/collector #(apply conj % chunk))
        (log/log :error e))
      (catch Exception e
        (log/log :error "Can not write" (pr-str chunk) "in chunk")
        (log/log :error e)))))

(defn watch-collector [path]
  (when chunking-enabled?
    (add-watch bdio/collector :watch-collector
               (fn [key atom old-state new-state]
                 (when (> (count new-state) chunk-size)
                   (let [new-chunk (take chunk-size new-state)
                         rest (drop chunk-size new-state)]
                     (log/log :info "new-chunk:" (pr-str new-chunk))
                     (log/log :info "rest of chunk:" (pr-str rest))
                     (reset! bdio/collector rest)
                     (.start (Thread. (fn []
                                        (process-chunk path new-chunk))))))))))