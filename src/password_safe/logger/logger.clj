(ns password-safe.logger.logger
  (:require [clojure.java.io :as io]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.core :as appenders]
            [config.core :refer [env]]))

(defn init []
  (when (-> env
            :logging
            :clean-logs?)
    (io/delete-file (-> env
                        :logging
                        :output-path) :quiet))

  (timbre/refer-timbre) ; set up timbre aliases

  ; The default setup is simple console logging.  We with to turn off console logging and
  ; turn on file logging to our chosen filename.
  (timbre/merge-config! {:appenders {:println {:enabled? (-> env
                                                             :logging
                                                             :print-logs?)}}})
  (timbre/merge-config! {:appenders {:spit (appenders/spit-appender {:fname (-> env
                                                                                :logging
                                                                                :output-path)})}})

  ; Set the lowest-level to output as :debug
  (timbre/set-level! (-> env
                         :logging
                         :output-level)))

(defn log [level & args]
  (when (-> env
            :logging
            :enabled?)
    (timbre/log level args)))