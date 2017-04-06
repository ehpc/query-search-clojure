(ns query-search.blog-search-test
  "Тестирование поиска по блогам."
  (:require [clojure.test :refer :all]
            [clojure.string :refer [includes?]]
            [query-search.blog-search :refer :all]))

(deftest search-test
  (testing "Поиск по блогам."
    (is (includes? (first @(search ["scala"])) "yablogs:author"))))
