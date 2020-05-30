(ns password-safe.core
  (:require
    [clojure.java.io :as io]
    [taoensso.timbre :as timbre] 
    [password-safe.database2.core :as db])
  (:gen-class))
  
(def log-file-name "log.txt")

(defn init-logging []
  (io/delete-file log-file-name :quiet)

  (timbre/refer-timbre) ; set up timbre aliases
  
  ; The default setup is simple console logging.  We with to turn off console logging and
  ; turn on file logging to our chosen filename.
  (timbre/set-config! [:appenders :standard-out   :enabled?] false)
  (timbre/set-config! [:appenders :spit           :enabled?] true)
  (timbre/set-config! [:shared-appender-config :spit-filename] log-file-name)
  (timbre/set-config! [:shared-appender-config :spit-filename] log-file-name)
  
  ; Set the lowest-level to output as :debug
  (timbre/set-level! :debug)
)


(defn -main [& args]
  (println "args are: " args)
  (init-logging)
  ;; (service/init!)
  ;; (interface/execute args)
)
