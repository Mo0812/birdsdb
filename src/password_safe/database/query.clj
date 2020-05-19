(ns password-safe.database.query
  (:require
   [clojure.spec.alpha :as s]
   [password-safe.database.filter :as filter]
   [password-safe.database.spec :as spec]))

(defn filter-items [db & xfs]
  (sequence (apply comp xfs) db))

(defn full-search [db needle]
  (filter (filter/some-includes? needle)
          db))

(s/fdef full-search
  :args (s/cat :db ::spec/db :needle string?)
  :ret ::spec/db)