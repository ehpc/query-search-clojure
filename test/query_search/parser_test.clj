(ns query-search.parser-test
  (:require [clojure.test :refer :all]
            [query-search.blog-search :refer [search]]
            [query-search.parser :refer :all]))

(deftest parse-test
  (testing "Извлечение данных о доменах из ответа поиска по блогам."
    (let [parsed (parse (search ["scala"]))]
      (is (contains? parsed "vk.com") "В ответе есть нужный домен.")
      (is (> (get parsed "vk.com") 3) "Домен встречается нужное количество раз."))))
