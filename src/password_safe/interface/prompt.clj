(ns password-safe.interface.prompt
  (:require [password-safe.interface.commands :as commands]))

(defn fetch-cmd []
  (loop [in (read-line)]
    (if (= in "exit")
      (println "Bye bye")
      (do
        (apply commands/execute (clojure.string/split in #" "))
        (recur (read-line))))))

(defn start []
  (fetch-cmd))