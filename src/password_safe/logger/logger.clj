(ns password-safe.logger.logger
  (:require [clojure.java.io :as io]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.core :as appenders]))

(def log-file-name "log.txt")

(defn load-config []
  (io/delete-file log-file-name :quiet)

  (timbre/refer-timbre) ; set up timbre aliases

  ; The default setup is simple console logging.  We with to turn off console logging and
  ; turn on file logging to our chosen filename.
  (timbre/merge-config! {:appenders {:println {:enabled? false}}})
  (timbre/merge-config! {:appenders {:spit    (appenders/spit-appender {:fname log-file-name})}})

  ; Set the lowest-level to output as :debug
  (timbre/set-level! :debug))

(defn log [level & args]
  (timbre/log level args))