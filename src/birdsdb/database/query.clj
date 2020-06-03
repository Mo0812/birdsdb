(ns birdsdb.database.query
  (:require
   [clojure.spec.alpha :as s]
   [birdsdb.database.filter :as filter]
   [birdsdb.database.spec :as spec]))

(defn filter-items [db & xfs]
  (sequence (apply comp xfs) db))

(defn full-search [db needle]
  (filter (filter/some-includes? needle)
          db))

(s/fdef full-search
  :args (s/cat :db ::spec/db :needle string?)
  :ret ::spec/db)