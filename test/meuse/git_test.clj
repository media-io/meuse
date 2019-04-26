(ns meuse.git-test
  (:require [meuse.git :refer :all]
            [meuse.helpers.fixtures :refer [tmp-dir]]
            [spy.assert :as assert]
            [spy.core :as spy]
            [clojure.test :refer :all])
  (:import meuse.git.LocalRepository))

(deftest git-local-repo-test
  (let [git (LocalRepository. tmp-dir "origin/master")]
    (with-redefs [clojure.java.shell/sh (spy/spy (spy/stub {:exit 0}))]
      (add git)
      (assert/called-once-with? clojure.java.shell/sh
                                "git" "-C" tmp-dir "add" "."))
    (with-redefs [clojure.java.shell/sh (spy/spy (spy/stub {:exit 0}))]
      (git-cmd git ["foo" "bar"])
      (assert/called-once-with? clojure.java.shell/sh
                                "git" "-C" tmp-dir "foo" "bar"))
    (with-redefs [clojure.java.shell/sh (spy/spy (spy/stub {:exit 0}))]
      (commit git "msg header" "msg-body")
      (assert/called-once-with? clojure.java.shell/sh
                                "git" "-C" tmp-dir "commit"
                                "-m" "msg header" "-m" "msg-body"))
    (with-redefs [clojure.java.shell/sh (spy/spy (spy/stub {:exit 0}))]
      (pull git)
      (assert/called-once-with? clojure.java.shell/sh
                                "git" "-C" tmp-dir "pull" "origin/master"))
    (with-redefs [clojure.java.shell/sh (spy/spy (spy/stub {:exit 0}))]
      (pull git)
      (assert/called-once-with? clojure.java.shell/sh
                                "git" "-C" tmp-dir "pull" "origin/master"))))
