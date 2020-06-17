(ns birdsdb.core
  (:require
   [clojure.tools.cli :refer [parse-opts]]
   [birdsdb.db.service :as service]
   [birdsdb.interface.prompt :as prompt]
   [birdsdb.interface.server :as server]
   [birdsdb.logger.logger :as log])
  (:use [clansi])
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
    
 (defn welcome-message []
  (let [logo (slurp "assets/bcib50.txt")
        letters (slurp "assets/bdblb50.txt")]
    (println (style logo :yellow))
    (println letters)))

(defn -main [& args]
  (welcome-message)
  (log/init)
  (service/start)

  (let [options (parse-opts args cli-options)]
    (when (-> options
              :options
              :server)
      (server/start))
    (when (-> options
              :options
              :prompt)
      (prompt/start))))