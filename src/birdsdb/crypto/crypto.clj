(ns birdsdb.crypto.crypto)

(java.security.Security/addProvider (org.bouncycastle.jce.provider.BouncyCastleProvider.))

(defn key-generator [length]
  (doto (java.security.KeyPairGenerator/getInstance "RSA")
    (.initialize length)))

(defn generate-key [length]
  (if (>= length 512)
    (.generateKeyPair (key-generator length))
    (throw (AssertionError. "Key length must be at least 512 bits long"))))

(defn decode-base64 [str]
  (.decode (java.util.Base64/getDecoder) str))

(defn encode-base64 [bytes]
  (.encodeToString (java.util.Base64/getEncoder) bytes))

(defn decrypt [message private-key]
  (let [cipher (doto (javax.crypto.Cipher/getInstance "RSA/ECB/PKCS1Padding")
                 (.init javax.crypto.Cipher/DECRYPT_MODE private-key))]
    (->> message
         decode-base64
         (.doFinal cipher)
         (map char)
         (apply str))))

(defn encrypt [message public-key]
  (encode-base64
   (let [cipher (doto (javax.crypto.Cipher/getInstance "RSA/ECB/PKCS1Padding")
                  (.init javax.crypto.Cipher/ENCRYPT_MODE public-key))]
     (.doFinal cipher (.getBytes message)))))

(defn read-key-file [file]
  (->> file
       (slurp)
       (.getBytes)
       (clojure.java.io/reader)
       (org.bouncycastle.openssl.PEMParser.)
       (.readObject)))

(defn read-public-key [file]
  "Reads PEM public key file and convert into usable public key
   Returns sun.security.rsa.RSAPublicKeyImpl
   Generate private PEM key with: `openssl genrsa -out MYKEY.pem 2048`
   Generate public key from private PEM key with: `openssl -in MYKEY.pem -pbout -out MYKEY.pub"
  (let [kd (read-key-file file)
        kf (java.security.KeyFactory/getInstance "RSA")
        spec (java.security.spec.X509EncodedKeySpec. (.getEncoded kd))]
    (.generatePublic kf spec)))

(defn read-key-pair [file]
  "Reads PEM private key from file and create a public/private keypair.
   Returns java.security.KeyPair"
  (let [kd (read-key-file file)]
    (.getKeyPair (org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter.) kd)))

(defn read-private-key [file]
  (.getPrivate (read-key-pair file)))

