(ns birdsdb.benchmark.benchmark
  (:require [birdsdb.db.ql :as ql]
            [birdsdb.db.db :as db]
            [birdsdb.db.db-test :as db-test]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [config.core :refer [env]])
  (:use [criterium.core]))

(def db-path "db/benchmark")

(defn get-options []
  (println (-> env
               :db))
  (println (-> env
               :io)))

(defn clean-up []
  (doseq [file (file-seq (io/file db-path))
          :when (not (.isDirectory file))]
    (io/delete-file file)))

(defn writing-benchmark [items]
  (clean-up)
  (db/init! db-path)
  (let [data (repeatedly items #(gen/generate (s/gen ::db-test/object)))]
    (with-progress-reporting
      (quick-bench
       (doseq [object data]
         (ql/insert-db object {:io true :db-path db-path}))))))