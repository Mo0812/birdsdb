(ns birdsdb.crypto.crypto-test
  (:require [clojure.test :refer :all]
            [birdsdb.crypto.crypto :refer :all]))

(def public-key-path "keys/birdsdb.pub")

(def private-key-path "keys/birdsdb.pem")

(deftest generate-key-test
  (is (= (type (generate-key 512)) java.security.KeyPair))
  (is (thrown? AssertionError (generate-key 511))))

(deftest read-public-key-test
  (is (= (type (read-public-key public-key-path)) sun.security.rsa.RSAPublicKeyImpl)))

(deftest read-key-pair-test
  (is (= (type (read-key-pair private-key-path)) java.security.KeyPair)))


(deftest read-private-key-test
  (is (= (type (read-private-key private-key-path)) sun.security.rsa.RSAPrivateCrtKeyImpl)))

(deftest encryption-test
  (let [message "I am a secret"]
    (is (= message (decrypt (encrypt message (read-public-key public-key-path)) (read-private-key private-key-path))))))