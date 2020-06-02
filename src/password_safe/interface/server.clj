(ns password-safe.interface.server
  (:require [password-safe.interface.commands :as commands]
            [clojure.java.io :as io]
            [config.core :refer [env]])
  (:import [java.net ServerSocket]))

(declare serve start stop)

(def server-state (atom nil))

(def server-thread
  (Thread. (fn []
             (serve (-> env
                        :server
                        :port)))))

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
  (str (commands/execute msg-in)))

(defn serve [port]
  (with-open [server-sock (ServerSocket. port)
              sock (.accept server-sock)]
    (loop []
      (if (Thread/interrupted)
        nil
        (let [msg-in (receive sock)]
          (.start (Thread. (fn [] (send sock (handler msg-in)))))
          (recur))))))

(defn start []
  (stop)
  (println "init server...")
  (reset! server-state server-thread)
  (println "starting server...")
  (.start @server-state))

(defn stop []
  (println "stopping server...")
  (when-not (nil? @server-state)
    (println "interrputing thread...")
    (.interrupt @server-state))
  (reset! server-state nil))