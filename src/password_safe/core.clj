(ns password-safe.core
  (:require
   [password-safe.database2.service :as service]
   [password-safe.interface.prompt :as prompt]
   [password-safe.logger.logger :as log])
  (:gen-class))

(defn -main [& args]
  (println "args are: " args)
  (log/init)
  (service/start)
  
  (when (boolean (some #{"-p" "--prompt"} args))
    (prompt/start)))