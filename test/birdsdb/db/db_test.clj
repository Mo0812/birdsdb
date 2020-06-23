(ns birdsdb.db.db-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [birdsdb.db.db :refer :all]))

(def db-test-path "db/test")

(def test-state {#uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608"
                 {:id #uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608", :ts 1590247125384, :deleted false, :object {:test 3}}
                 #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098"
                 {:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098"
                  :ts 1590184832479
                  :object {:title "youtube.com", :username "robert", :password "MeinPasswort"}}
                 #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f"
                 {:id #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f"
                  :ts 1590184764635
                  :object {:title "asos.com", :username "ulf", :password "abc"}}})

(s/def ::object map?)

(def object-generator (gen/map (s/gen keyword?) (s/gen string?)))

(s/def ::id uuid?)
(s/def ::ts int?)
(s/def ::deleted boolean?)

(s/def ::entry (s/keys :req-un [::object]
                       :opt-un [::id ::deleted ::ts]))

(s/def ::db-entry (s/keys :req-un [::id ::ts ::object]
                          :opt-un [::deleted]))

(s/def ::db (s/coll-of (s/cat :id uuid? :entry ::db-entry)))

(def create-db-entry-test-check
  (prop/for-all [v (s/gen ::entry)]
                (let [[id db-entry] (create-db-entry v)]
                  (and
                   (s/valid? ::db-entry db-entry)
                   (uuid? id)
                   (= id (:id db-entry))))))

(defspec create-db-entry-test-check-spec
  5
  create-db-entry-test-check)

(s/fdef init!
  :args (s/cat)
  :ret ::db)

(deftest init!-test
  (is (init! db-test-path) test-state)
  (is (s/valid? ::db @db)))

(deftest init!-test-spec
  (is (stest/check `init!)))

(s/fdef add!
  :args (s/cat :entry ::entry)
  :ret future?)

(deftest add!-test
  (let [fut (add! {:object {:test "test"}} {:io false :db-path db-test-path})]
    (is @fut @db)
    (is (s/valid? ::db @fut))
    (is (s/valid? ::db @db))))

(deftest add!-test-spec
  (is (stest/check `add!)))

(def add!-test-check
  (prop/for-all [v (s/gen (s/or ::entry ::db-entry))]
                (let [fut (add! v {:io false :db-path db-test-path})]
                  (and
                   (future? fut)
                   (s/valid? ::db @fut)))))

(defspec add!-test-check-spec
  5
  add!-test-check)
