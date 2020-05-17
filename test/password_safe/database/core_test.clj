(ns password-safe.database.core-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]
            [password-safe.database.core :refer :all]))

(def test-state [{:title "asos.com", :username "ulf", :password "abc", :id #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f"}
                 {:title "youtube.com", :username "robert", :password "Password123", :id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098"}])

(def db-item-sample
  (gen/hash-map :title gen/string
                :username gen/string
                :password gen/string
                :id (gen/one-of [gen/uuid gen/string])))

(deftest read-db-dump-test
  (is (read-db-dump db-path) test-state))

(deftest write-db-dump-test
  (let [db-test-path "db-test.json"]
    (clojure.java.io/delete-file db-test-path true)
    (write-db-dump db-test-path test-state)
    (is (slurp db-path) (slurp db-test-path))))

(defspec generate-id-test-check
  20
  (prop/for-all [v db-item-sample]
                (let [item (generate-id v)]
                  (and (uuid? (:id item))))))

(deftest get-index-by-id-test
  (is (get-index-by-id test-state #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f") 0))

(deftest add!-test
  (let [new-item (gen/generate db-item-sample)]
    (add! db new-item)
    (await db)
    (is (last @db) new-item)))

(defspec add!-test-check
  20
  (prop/for-all [v db-item-sample]
                (add! db v)
                (await db)
                (is (last @db) v)))

(deftest update!-test
  (let [id "bc1ccf0c-815b-4099-b02c-703acd3fc29f"
        new-item (gen/generate db-item-sample)]
    (update! db id new-item)
    (await db)))