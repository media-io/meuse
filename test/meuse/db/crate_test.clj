(ns meuse.db.crate-test
  (:require [meuse.config :refer [config]]
            [meuse.db :refer [database]]
            [meuse.db.crate :refer :all]
            [meuse.helpers.db :refer :all]
            [meuse.helpers.fixtures :refer :all]
            [meuse.message :as message]
            [mount.core :as mount]
            [clojure.java.jdbc :as jdbc]
            [clojure.test :refer :all])
  (:import clojure.lang.ExceptionInfo))

(use-fixtures :once db-fixture)
(use-fixtures :each table-fixture)

(deftest ^:integration new-crate-test
  (let [request {:database database}
        crate {:metadata {:name "test1"
                          :vers "0.1.3"
                          :yanked false}}]
    (new-crate request crate)
    (is (thrown-with-msg? ExceptionInfo
                          #"already exists$"
                          (new-crate request crate)))
    (test-db-state database {:crate-name "test1"
                             :version-version "0.1.3"
                             :version-yanked false
                             :version-description nil})
    (new-crate request (assoc-in crate [:metadata :vers] "2.0.0"))
    (test-db-state database {:crate-name "test1"
                             :version-version "2.0.0"
                             :version-yanked false
                             :version-description nil})))

(deftest ^:integration update-yank-test
  (let [request {:database database}
        crate {:metadata {:name "test1"
                          :vers "0.1.3"
                          :yanked false}}]
    (new-crate request crate)
    (test-db-state database {:crate-name "test1"
                             :version-version "0.1.3"
                             :version-yanked false
                             :version-description nil})
    (update-yank request "test1" "0.1.3" true)
    (test-db-state database {:crate-name "test1"
                             :version-version "0.1.3"
                             :version-yanked true
                             :version-description nil})
    (update-yank request "test1" "0.1.3" false)
    (test-db-state database {:crate-name "test1"
                             :version-version "0.1.3"
                             :version-yanked false
                             :version-description nil})))
