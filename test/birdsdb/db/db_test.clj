(ns birdsdb.db.db-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]
            [birdsdb.db.db :refer :all]))

(def test-state {#uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608" {:test 3}, #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098" {:title "youtube.com", :username "robert", :password "MeinPasswort"}, #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f" {:title "asos.com", :username "ulf", :password "abc"}})

(def object-sample
  (gen/map gen/keyword (gen/one-of [gen/string gen/small-integer])))

(def db-entry-sample
  (gen/hash-map :id gen/uuid
                :ts (gen/large-integer* {:min 1})
                :deleted gen/boolean
                :object object-sample))
                
(defspec create-db-entry-test-check
  20
  (prop/for-all [v db-entry-sample]
    (let [db-entry (create-db-entry v)]
      (and 
        (uuid? (:id db-entry))
        (boolean? (:deleted db-entry))
        (not (nil? (:object db-entry)))))))