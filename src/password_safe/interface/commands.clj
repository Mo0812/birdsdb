(ns password-safe.interface.commands
  (:require [password-safe.database2.ql :as ql]
            [password-safe.generator.core :as generator]))

(defmulti interface-commands (fn [cmd _]
                               cmd))

(defmethod interface-commands "search" [cmd args]
  (ql/select))

(defmethod interface-commands "generate" [cmd [len & pieces]]
  (generator/generate-password len))

(defmethod interface-commands :default [cmd args]

  (str "The cmd '" cmd "' is not supported or misspelled, please try again or use 'help' for showing the available commands"))

(defn execute
  ([cmd & args]
   (println "getting: " cmd args)
   (println (interface-commands cmd args))))