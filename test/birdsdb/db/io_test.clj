(ns birdsdb.db.io-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [birdsdb.db.io :refer :all]))
          
(def db-test-path "db_test")

(def test-file-path "db_test/chunk-2de31d4d-afba-48dd-8d7e-c50b37406e54.json")
          
(def test-file-contents '({:id #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f", :ts 1590184764635, :object {:title "asos.com", :username "ulf", :password "abc"}} {:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098", :ts 1590184764412, :object {:title "youtube.com", :username "robert", :password "Password123"}}))

(def full-file-dump '({:id #uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608", :ts 1590247125384, :deleted false, :object {:test 3}} {:id #uuid "e70c1425-a25d-48ff-b889-f1bc91ead4d2", :ts 1590244489576, :object {:title "facebook.com", :username "john", :password "abcdefgh123"}} {:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098", :ts 1590184832479, :object {:title "youtube.com", :username "robert", :password "MeinPasswort"}} {:id #uuid "e70c1425-a25d-48ff-b889-f1bc91ead4d2", :ts 1590244517802, :deleted true, :object {:title "facebook.com", :username "john", :password "abcdefgh123"}} {:id #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f", :ts 1590184764635, :object {:title "asos.com", :username "ulf", :password "abc"}} {:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098", :ts 1590184764412, :object {:title "youtube.com", :username "robert", :password "Password123"}}))

(def full-db-state {#uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608" [{:id #uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608", :ts 1590247125384, :deleted false, :object {:test 3}}], #uuid "e70c1425-a25d-48ff-b889-f1bc91ead4d2" [{:id #uuid "e70c1425-a25d-48ff-b889-f1bc91ead4d2", :ts 1590244489576, :object {:title "facebook.com", :username "john", :password "abcdefgh123"}} {:id #uuid "e70c1425-a25d-48ff-b889-f1bc91ead4d2", :ts 1590244517802, :deleted true, :object {:title "facebook.com", :username "john", :password "abcdefgh123"}}], #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098" [{:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098", :ts 1590184832479, :object {:title "youtube.com", :username "robert", :password "MeinPasswort"}} {:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098", :ts 1590184764412, :object {:title "youtube.com", :username "robert", :password "Password123"}}], #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f" [{:id #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f", :ts 1590184764635, :object {:title "asos.com", :username "ulf", :password "abc"}}]})

(def current-db-state {#uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608" {:id #uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608", :ts 1590247125384, :deleted false, :object {:test 3}}, #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098" {:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098", :ts 1590184832479, :object {:title "youtube.com", :username "robert", :password "MeinPasswort"}}, #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f" {:id #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f", :ts 1590184764635, :object {:title "asos.com", :username "ulf", :password "abc"}}})

(s/def ::object map?)

(def object-generator (gen/map (s/gen keyword?) (s/gen string?)))

(s/def ::id uuid?)
(s/def ::ts int?)
(s/def ::deleted boolean?)

(s/def ::file-entry (s/keys :req-un [::id ::ts ::object]
                          :opt-un [::deleted]))

(s/def ::file-dump (s/coll-of ::file-entry))

(s/def ::db-with-revisions (s/coll-of (s/cat :id uuid? :entry (s/coll-of ::file-entry))))

(s/def ::db-current (s/coll-of (s/cat :id uuid? :entry ::file-entry)))
          
(deftest read-file-test
  (let [file-entry (read-file test-file-path)]
    (is  (= file-entry test-file-contents))
    (is (s/valid? ::file-dump file-entry))))
    
(deftest read-db-test
  (let [file-dump (read-db db-test-path)]
    (is (= file-dump full-file-dump))
    (is (s/valid? ::file-dump file-dump))))
    
(deftest write-db-data-test
  (let [id (java.util.UUID/randomUUID)
        object (gen/generate (gen/such-that #(not (:deleted %)) (s/gen ::file-entry)))]
        (write-db-data db-test-path [object] id)
        (is (= (read-db (str db-test-path id ".json")) [object]))
        (is (s/valid? ::file-dump (read-db (str db-test-path id ".json"))))))
        
(deftest receive-all-test
  (let [db (receive-all db-test-path)]
    (is (= db full-db-state))
    (is (s/valid? ::db-with-revisions db))))
    
(deftest receive-current-state-test
  (let [db (receive-current-state db-test-path)]
    (is (= db current-db-state))
    (is (s/valid? ::db-current db))))