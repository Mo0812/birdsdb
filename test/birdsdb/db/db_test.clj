(ns birdsdb.db.db-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [birdsdb.db.db :refer :all]
            [birdsdb.db.specs :as bdbspecs]
            [birdsdb.db.test_data :as test-data]))

(def create-db-entry-test-check
  (prop/for-all [v (s/gen ::bdbspecs/entry)]
                (let [[id db-entry] (create-db-entry v)]
                  (and
                   (s/valid? ::bdbspecs/db-entry db-entry)
                   (uuid? id)
                   (= id (:id db-entry))))))

(defspec create-db-entry-test-check-spec
  5
  create-db-entry-test-check)

(s/fdef init!
  :args (s/cat)
  :ret ::bdbspecs/db)

(deftest init!-test
  (is (init! test-data/db-test-path) test-data/test-state)
  (is (s/valid? ::bdbspecs/db @db)))

(deftest init!-test-spec
  (is (stest/check `init!)))

(s/fdef add!
  :args (s/cat :entry ::bdbspecs/entry)
  :ret future?)

(deftest add!-test
  (let [fut (add! {:object {:test "test"}} {:io false :db-path test-data/db-test-path})]
    (is @fut @db)
    (is (s/valid? ::bdbspecs/db @fut))
    (is (s/valid? ::bdbspecs/db @db))))

(deftest add!-test-spec
  (is (stest/check `add!)))

(def add!-test-check
  (prop/for-all [v (s/gen (s/or ::bdbspecs/entry ::bdbspecs/db-entry))]
                (let [fut (add! v {:io false :db-path test-data/db-test-path})]
                  (and
                   (future? fut)
                   (s/valid? ::bdbspecs/db @fut)))))

(defspec add!-test-check-spec
  5
  add!-test-check)
