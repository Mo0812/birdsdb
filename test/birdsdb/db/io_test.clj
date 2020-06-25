(ns birdsdb.db.io-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [birdsdb.db.io :refer :all]
            [birdsdb.db.specs :as bdbspecs]
            [birdsdb.db.test_data :as test-data])
  (:use clojure.data))

(deftest compose-filename-test
  (let [id (gen/generate (s/gen ::bdbspecs/id))]
    (is (= (compose-filename id) (str id ".edn")))))

(deftest read-file-test
  (let [file-entry (read-file test-data/test-file-path)]
    (is  (= file-entry test-data/test-file-contents))
    (is (s/valid? ::bdbspecs/file-dump file-entry))))

(deftest read-db-test
  (let [file-dump (read-db test-data/db-test-path)]
    (is (= [nil nil test-data/full-file-dump] (clojure.data/diff file-dump test-data/full-file-dump)))
    (is (s/valid? ::bdbspecs/file-dump file-dump))))

(deftest write-db-data-test
  (let [id (java.util.UUID/randomUUID)
        path (str test-data/db-test-path "/" id ".edn")
        object (gen/generate (gen/such-that #(not (:deleted %)) (s/gen ::bdbspecs/file-entry)))]
    (write-db-data test-data/db-test-path [object] id)
    (is (= (read-file path) [object]))
    (is (s/valid? ::bdbspecs/file-dump (read-file path)))
    (clojure.java.io/delete-file path)))

(deftest receive-all-test
  (let [db (receive-all test-data/db-test-path)]
    (is (= [nil nil test-data/full-db-state] (clojure.data/diff db test-data/full-db-state)))
    (is (s/valid? ::bdbspecs/db-with-revisions db))))

(deftest receive-current-state-test
  (let [db (receive-current-state test-data/db-test-path)]
    (is (= [nil nil test-data/current-db-state] (clojure.data/diff db test-data/current-db-state)))
    (is (s/valid? ::bdbspecs/db-current db))))