(ns birdsdb.interface.commands
  (:require [birdsdb.db.ql :as ql]
            [clojure.data.json :as json]))

(defmulti interface-commands (fn [cmd _]
                               cmd))

(defmethod interface-commands "select" [_ args]
  (let [response (if (nil? args)
                   (ql/select-db)
                   (ql/select-db args))]
    (json/write-str [response])))

(defmethod interface-commands "insert" [_ objects]
  (println objects)
  (for [object objects]
    (ql/insert-db object)))

(defmethod interface-commands "update" [_ [id new-object]]
  (let [id (java.util.UUID/fromString id)
        new-object (json/read-str new-object
                                  :key-fn keyword)]
    (ql/update-db id new-object)))

(defmethod interface-commands "delete" [_ ids]
  (for [id ids
        :let [uuid (java.util.UUID/fromString id)]]
    (ql/delete-db uuid)))

(defmethod interface-commands :default [cmd args]
  (str "The cmd '" cmd "' is not supported or misspelled, please try again or use 'help' for showing the available commands"))

(defn execute-command
  ([cmd & args]
   (interface-commands cmd args)))

(defn execute [cmd]
  (cond
    (string? cmd) (apply execute-command (first (clojure.string/split cmd #" ")) (json/read-str (clojure.string/join " " (rest (clojure.string/split cmd #" "))) :key-fn keyword))
    (vector? cmd) (apply execute-command cmd)))

