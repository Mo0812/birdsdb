(ns birdsdb.core
  (:require
   [birdsdb.database2.service :as service]
   [birdsdb.interface.prompt :as prompt]
   [birdsdb.interface.server :as server]
   [birdsdb.logger.logger :as log])
  (:gen-class))

(defn -main [& args]
  (println "args are: " args)
  (log/init)
  (service/start)

  (when (boolean (some #{"-p" "--prompt"} args))
    (prompt/start))
  (when (boolean (some #{"-s" "--server"} args))
    (server/start)))