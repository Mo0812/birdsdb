(ns password-safe.core
  (:require
   [clojure.java.io :as io]
   [taoensso.timbre :as timbre]
   [taoensso.timbre.appenders.core :as appenders]
   [password-safe.database2.core :as db])
  (:gen-class))

(defn init-logging [log-file-name]
  (io/delete-file log-file-name :quiet)

  (timbre/refer-timbre) ; set up timbre aliases

  ; The default setup is simple console logging.  We with to turn off console logging and
  ; turn on file logging to our chosen filename.
  (timbre/merge-config! {:appenders {:println {:enabled? false}}})
  (timbre/merge-config! {:appenders {:spit    (appenders/spit-appender {:fname log-file-name})}})

  ; Set the lowest-level to output as :debug
  (timbre/set-level! :debug))


(defn -main [& args]
  (println "args are: " args)
  (init-logging "log.txt")
  ;; (service/init!)
  ;; (interface/execute args)
  )
