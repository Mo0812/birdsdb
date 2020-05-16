(ns password-safe.database.query
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]
   [clojure.spec.test.alpha :as stest]
   [password-safe.database.core :as db]
   [password-safe.database.filter :as filter]))

(s/def ::title string?)
(s/def ::username string?)
(s/def ::password string?)
(s/def ::id uuid?)
(s/def ::db-item (s/keys :req-un [::title ::username ::password ::id]))
(s/def ::db (s/coll-of ::db-item))

(defn filter-items [db & xfs]
  (sequence (apply comp xfs) db))

(defn full-search [db needle]
  (filter (filter/some-includes? needle)
          db))

(s/fdef full-search
  :args (s/cat :db ::db :needle string?)
  :ret ::db)
