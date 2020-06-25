(ns birdsdb.db.chunker
  (:require [birdsdb.db.io :as bdb-io]
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

(def collector (atom {:relevant #{}
                      :processed #{}}))

(defn process-chunk [path chunk]
  (log/log :info "processing chunk:" (pr-str chunk))
  (let [chunk-contents (for [file-id chunk]
                         (flatten (read-string (slurp (io/file path (bdb-io/compose-filename file-id))))))]
    (log/log :info "chunk contents:" (pr-str chunk-contents))
    (try
      (log/log :info "try to write" (pr-str chunk) "in chunk")
      (bdb-io/write-db-data path (flatten chunk-contents) (str "chunk-" (java.util.UUID/randomUUID)))
      (doseq [file-id chunk]
        (log/log :info "deleting:" file-id)
        (io/delete-file (io/file path (bdb-io/compose-filename file-id))))
      (catch java.io.FileNotFoundException e
        (swap! collector update :relevant #(
                                            conj % chunk))
        (log/log :error e))
      (catch Exception e
        (log/log :error "Can not write" (pr-str chunk) "in chunk")
        (log/log :error e)))))

(defn watch-collector
  ([path]
   (watch-collector path chunk-size chunking-enabled?))
  ([path chunk-size enabled?]
   (when enabled?
     (add-watch bdb-io/protocol :watch-collector
                (fn [key atom old-state new-state]
                  (let [relevant-state (filter #(and
                                                 (not (clojure.string/starts-with? % "chunk-"))
                                                 (not (contains? (:processed @collector) %))) new-state)]
                    (swap! collector update :relevant #(apply conj % relevant-state))
                    (println "relevant:" relevant-state)
                    (println "current collector" @collector)
                    (when (> (count (:relevant @collector)) chunk-size)
                      (let [new-chunk (take chunk-size (:relevant @collector))
                            rest (drop chunk-size (:relevant @collector))]
                        (log/log :info "new-chunk:" (pr-str new-chunk))
                        (log/log :info "rest of chunk:" (pr-str rest))
                        (swap! collector assoc :relevant (set rest))
                        (swap! collector update :processed #(apply conj % new-chunk))
                        (.start (Thread. (fn []
                                           (process-chunk path new-chunk))))))))))))