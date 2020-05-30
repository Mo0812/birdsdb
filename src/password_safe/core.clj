(ns password-safe.core
  (:require
   [password-safe.database2.core :as db]
   [password-safe.logger.logger :as log])
  (:gen-class))


(defn -main [& args]
  (println "args are: " args)
  (log/load-config)
  ;; (service/init!)
  ;; (interface/execute args)
  )
