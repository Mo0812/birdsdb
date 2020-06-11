(ns birdsdb.core
  (:require
   [clojure.tools.cli :refer [parse-opts]]
   [birdsdb.db.service :as service]
   [birdsdb.interface.prompt :as prompt]
   [birdsdb.interface.server :as server]
   [birdsdb.logger.logger :as log])
  (:gen-class))

(def cli-options
  [["-s" "--server" "Start server"
    :default false]
   ["-p" "--prompt" "Start prompt"
    :default false]
   [nil "--port PORT" "Set server port"
    :default 50937
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]])

(defn -main [& args]
  (println "args are: " args)
  (log/init)
  (service/start)

  (let [options (parse-opts args cli-options)]
    (println "parsed cli options:")
    (println options)
    (when (-> options
              :options
              :server)
      (server/start))
    (when (-> options
              :options
              :prompt)
      (prompt/start))))