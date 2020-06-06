(ns birdsdb.db.filter)

(defn is-id? [id]
  (filter #(= (key %) id)))

(defn is-field? [k v]
  (filter #(= (k (val %)) v)))

(defn some-key? [k]
  (filter (fn [[_ obj]]
            (some #(clojure.string/includes? (str %) (str k)) (map key obj)))))

(defn some-field? [v]
  (filter (fn [[_ obj]]
            (some #(clojure.string/includes? % v) (map val obj)))))
