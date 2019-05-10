(ns meuse.api.meuse.user-test
  (:require [meuse.api.meuse.http :refer :all]
            [meuse.api.meuse.user :refer :all]
            [meuse.auth.password :as password]
            [meuse.db :refer [database]]
            [meuse.db.user :as user-db]
            [meuse.db.role :as role-db]
            [meuse.helpers.fixtures :refer :all]
            [clojure.test :refer :all])
  (:import clojure.lang.ExceptionInfo))

(use-fixtures :once db-fixture)
(use-fixtures :each table-fixture)

(deftest ^:integration new-user-test
  (let [user {:name "mathieu"
              :password "foobarbaz"
              :description "it's me mathieu"
              :role "admin"}
        request {:database database
                 :action :new-user
                 :body user}]
    (is (= {:status 200} (meuse-api! request)))
    (let [user-db (user-db/get-user-by-name database "mathieu")
          admin-role (role-db/get-admin-role database)]
      (is (uuid? (:user-id user-db)))
      (is (= (:name user) (:user-name user-db)))
      (is (password/check (:password user) (:user-password user-db)))
      (is (= (:description user) (:user-description user-db)))
      (is (= (:role-id admin-role) (:user-role-id user-db))))
    (is (thrown-with-msg? ExceptionInfo
                          #"already exists$"
                          (meuse-api! request))))
  (testing "invalid parameters"
    (is (thrown-with-msg?
         ExceptionInfo
         #"invalid parameters"
         (meuse-api! {:action :new-user
                      :body {:name "mathieu"
                             :password "foobar"
                             :description "it's me mathieu"
                             :role "admin"}})))
    (is (thrown-with-msg?
         ExceptionInfo
         #"invalid parameters"
         (meuse-api! {:action :new-user
                      :body {:name "mathieu"
                             :description "it's me mathieu"
                             :role "admin"}})))
    (is (thrown-with-msg?
         ExceptionInfo
         #"invalid parameters"
         (meuse-api! {:action :new-user
                      :body {:name "mathieu"
                             :description "it's me mathieu"
                             :role "lol"}})))))
