(ns password-safe.generator.core)

(defn generate-password [len]
  (let [chars-between #(map char (range (int %1) (inc (int %2))))
        chars (concat (chars-between \0 \9)
                      (chars-between \a \z)
                      (chars-between \A \Z)
                      [\_])
        password (take len (repeatedly #(rand-nth chars)))]
    (reduce str password)))