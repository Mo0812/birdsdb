(ns password-safe.interface.server
  (:require [password-safe.interface.commands :as commands]
            [clojure.java.io :as io])
  (:import [java.net ServerSocket]))
  
(declare serve)
  
(defonce server-thread (Thread. (fn [] (serve 8080))))
  
(defn receive
  "Read a line of textual data from the given socket"
  [socket]
  (.readLine (io/reader socket)))

(defn send
  "Send the given string message out over the given socket"
  [socket msg]
  (let [writer (io/writer socket)]
      (.write writer msg)
      (.flush writer)))
      
(defn handler [msg-in]
  (str (apply commands/execute (clojure.string/split msg-in #" "))))

(defn serve [port]
  (with-open [server-sock (ServerSocket. port)
              sock (.accept server-sock)]
    (loop []
      (if (Thread/interrupted)
        nil
        (let [msg-in (receive sock)
              msg-out (handler msg-in)]
            (send sock msg-out)
            (recur))))))
          
(defn start []
  (.start server-thread))
  
(defn stop []
  (.interrupt server-thread))