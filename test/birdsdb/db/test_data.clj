(ns birdsdb.db.test_data)

(def db-test-path "db/test")

(def test-state {#uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608"
                 {:id #uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608", :ts 1590247125384, :deleted false, :object {:test 3}}
                 #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098"
                 {:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098"
                  :ts 1590184832479
                  :object {:title "youtube.com", :username "robert", :password "MeinPasswort"}}
                 #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f"
                 {:id #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f"
                  :ts 1590184764635
                  :object {:title "asos.com", :username "ulf", :password "abc"}}})


(def test-file-path "db/test/chunk-2de31d4d-afba-48dd-8d7e-c50b37406e54.edn")

(def test-file-contents '({:id #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f", :ts 1590184764635, :object {:title "asos.com", :username "ulf", :password "abc"}} {:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098", :ts 1590184764412, :object {:title "youtube.com", :username "robert", :password "Password123"}}))

(def full-file-dump '({:id #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f"
                       :ts 1590184764635
                       :object {:title "asos.com", :username "ulf", :password "abc"}}
                      {:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098"
                       :ts 1590184764412
                       :object {:title "youtube.com", :username "robert", :password "Password123"}}
                      {:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098"
                       :ts 1590184832479
                       :object {:title "youtube.com", :username "robert", :password "MeinPasswort"}}
                      {:id #uuid "e70c1425-a25d-48ff-b889-f1bc91ead4d2"
                       :ts 1590244517802
                       :deleted true
                       :object {:title "facebook.com", :username "john", :password "abcdefgh123"}}
                      {:id #uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608", :ts 1590247125384, :deleted false, :object {:test 3}}
                      {:id #uuid "e70c1425-a25d-48ff-b889-f1bc91ead4d2"
                       :ts 1590244489576
                       :object {:title "facebook.com", :username "john", :password "abcdefgh123"}}))

(def full-db-state {#uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f"
                    [{:id #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f"
                      :ts 1590184764635
                      :object {:title "asos.com", :username "ulf", :password "abc"}}]
                    #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098"
                    [{:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098"
                      :ts 1590184764412
                      :object {:title "youtube.com", :username "robert", :password "Password123"}}
                     {:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098"
                      :ts 1590184832479
                      :object {:title "youtube.com", :username "robert", :password "MeinPasswort"}}]
                    #uuid "e70c1425-a25d-48ff-b889-f1bc91ead4d2"
                    [{:id #uuid "e70c1425-a25d-48ff-b889-f1bc91ead4d2"
                      :ts 1590244517802
                      :deleted true
                      :object {:title "facebook.com", :username "john", :password "abcdefgh123"}}
                     {:id #uuid "e70c1425-a25d-48ff-b889-f1bc91ead4d2"
                      :ts 1590244489576
                      :object {:title "facebook.com", :username "john", :password "abcdefgh123"}}]
                    #uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608"
                    [{:id #uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608", :ts 1590247125384, :deleted false, :object {:test 3}}]})

(def current-db-state {#uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f"
                       {:id #uuid "bc1ccf0c-815b-4099-b02c-703acd3fc29f"
                        :ts 1590184764635
                        :object {:title "asos.com", :username "ulf", :password "abc"}}
                       #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098"
                       {:id #uuid "220af2f7-99a5-42e6-9785-7af7d2fcb098"
                        :ts 1590184832479
                        :object {:title "youtube.com", :username "robert", :password "MeinPasswort"}}
                       #uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608"
                       {:id #uuid "fbeeb1a9-cecd-4c77-8d7b-5c1c6eba0608", :ts 1590247125384, :deleted false, :object {:test 3}}})