(ns birdsdb.db.specs
  (:require [clojure.spec.alpha :as s]))


(s/def ::object map?)

(s/def ::id uuid?)
(s/def ::ts int?)
(s/def ::deleted boolean?)

(s/def ::file-entry (s/keys :req-un [::id ::ts ::object]
                            :opt-un [::deleted]))

(s/def ::file-dump (s/coll-of ::file-entry))

(s/def ::entry (s/keys :req-un [::object]
                       :opt-un [::id ::deleted ::ts]))

(s/def ::db-entry (s/keys :req-un [::id ::ts ::object]
                          :opt-un [::deleted]))

(s/def ::db (s/coll-of (s/cat :id uuid? :entry ::db-entry)))

(s/def ::db-with-revisions (s/map-of ::id (s/coll-of ::file-entry)))

(s/def ::db-current (s/map-of ::id ::file-entry))