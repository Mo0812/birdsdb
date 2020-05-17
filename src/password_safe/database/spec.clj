(ns password-safe.database.spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::title string?)
(s/def ::username string?)
(s/def ::password string?)
(s/def ::id uuid?)
(s/def ::db-item (s/keys :req-un [::title ::username ::password ::id]))
(s/def ::db (s/coll-of ::db-item))

