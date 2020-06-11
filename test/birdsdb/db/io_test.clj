(ns birdsdb.db.io-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [birdsdb.db.io :refer :all]))

(def db-test-path "db_test")

(def test-file-path "db_test/chunk-2de31d4d-afba-48dd-8d7e-c50b37406e54.edn")

(def test-file-contents '({:id #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f", :ts 1590184764635, :object {:title "asos.com", :username "ulf", :password "abc"}} {:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098", :ts 1590184764412, :object {:title "youtube.com", :username "robert", :password "Password123"}}))

(def full-file-dump '({:id #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f"
                       :ts 1590184764635
                       :object {:title "asos.com", :username "ulf", :password "abc"}}
                      {:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098"
                       :ts 1590184764412
                       :object {:title "youtube.com", :username "robert", :password "Password123"}}
                      {:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098"
                       :ts 1590184832479
                       :object {:title "youtube.com", :username "robert", :password "MeinPasswort"}}
                      {:id #uuid "e70c1425-a25d-48ff-b889-f1bc91ead4d2"
                       :ts 1590244517802
                       :deleted true
                       :object {:title "facebook.com", :username "john", :password "abcdefgh123"}}
                      {:id #uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608", :ts 1590247125384, :deleted false, :object {:test 3}}
                      {:id #uuid "e70c1425-a25d-48ff-b889-f1bc91ead4d2"
                       :ts 1590244489576
                       :object {:title "facebook.com", :username "john", :password "abcdefgh123"}}))

(def full-db-state {#uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f"
                    [{:id #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f"
                      :ts 1590184764635
                      :object {:title "asos.com", :username "ulf", :password "abc"}}]
                    #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098"
                    [{:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098"
                      :ts 1590184764412
                      :object {:title "youtube.com", :username "robert", :password "Password123"}}
                     {:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098"
                      :ts 1590184832479
                      :object {:title "youtube.com", :username "robert", :password "MeinPasswort"}}]
                    #uuid "e70c1425-a25d-48ff-b889-f1bc91ead4d2"
                    [{:id #uuid "e70c1425-a25d-48ff-b889-f1bc91ead4d2"
                      :ts 1590244517802
                      :deleted true
                      :object {:title "facebook.com", :username "john", :password "abcdefgh123"}}
                     {:id #uuid "e70c1425-a25d-48ff-b889-f1bc91ead4d2"
                      :ts 1590244489576
                      :object {:title "facebook.com", :username "john", :password "abcdefgh123"}}]
                    #uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608"
                    [{:id #uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608", :ts 1590247125384, :deleted false, :object {:test 3}}]})

(def current-db-state {#uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f"
                       {:id #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f"
                        :ts 1590184764635
                        :object {:title "asos.com", :username "ulf", :password "abc"}}
                       #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098"
                       {:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098"
                        :ts 1590184832479
                        :object {:title "youtube.com", :username "robert", :password "MeinPasswort"}}
                       #uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608"
                       {:id #uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608", :ts 1590247125384, :deleted false, :object {:test 3}}})

(s/def ::id uuid?)
(s/def ::ts int?)
(s/def ::deleted boolean?)
(s/def ::object-key keyword?)
(s/def ::object-value (s/or :string string? :int int? :double double?))

(s/def ::object map?)

(s/def ::file-entry (s/keys :req-un [::id ::ts ::object]
                            :opt-un [::deleted]))

(s/def ::file-dump (s/coll-of ::file-entry))

(s/def ::db-with-revisions (s/map-of ::id (s/coll-of ::file-entry)))

(s/def ::db-current (s/map-of ::id ::file-entry))

(deftest compose-filename-test
  (let [id (gen/generate (s/gen ::id))]
    (is (= (compose-filename id) (str id ".edn")))))

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
        path (str db-test-path "/" id ".edn")
        object (gen/generate (gen/such-that #(not (:deleted %)) (s/gen ::file-entry)))]
    (write-db-data db-test-path [object] id)
    (is (= (read-file path) [object]))
    (is (s/valid? ::file-dump (read-file path)))
    (clojure.java.io/delete-file path)))

(deftest receive-all-test
  (let [db (receive-all db-test-path)]
    (is (= db full-db-state))
    (is (s/valid? ::db-with-revisions db))))

(deftest receive-current-state-test
  (let [db (receive-current-state db-test-path)]
    (is (= db current-db-state))
    (is (s/valid? ::db-current db))))