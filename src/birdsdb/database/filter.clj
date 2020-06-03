(ns birdsdb.database.filter)

(defn item-includes?
  [key val]
  (filter (comp #(clojure.string/includes? % val) key)))

(defn some-includes?
  ([needle]
   (fn [m]
     (some-includes? needle m)))
  ([needle m]
   (some #(clojure.string/includes? % needle) (map val m))))