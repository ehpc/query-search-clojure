(ns query-search.parser-test
  "Тестирование парсера ответа поиска по блогам."
  (:require [clojure.test :refer :all]
            [query-search.blog-search :refer [search]]
            [query-search.parser :refer :all]))

(deftest parse-test
  (testing "Извлечение данных о доменах из ответа поиска по блогам."
    (is
      (some
        #(= (first %) "vk.com")
        (parse (first @(search ["scala"]))))
      "В ответе есть нужный домен.")))
