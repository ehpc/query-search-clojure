(ns query-search.crawler-test
  "Тестирование загрузчика веб-страниц."
  (:require [clojure.test :refer :all]
            [clojure.string :refer [includes?]]
            [query-search.crawler :refer :all]))

(deftest crawl-test
  (testing "Загрузка веб-страницы."
    (is
      (includes?
        (first
          (crawl ["https://httpbin.org/get?param=test1"])) "\"test1\"")
      "Одна страница загружается успешно.")
    (is
      (=
        (reduce
          #(str (re-find #"\"test\d\"" %2) %1)
          ""
          (crawl ["https://httpbin.org/get?param=test1" "https://httpbin.org/get?param=test2"]))
        "\"test2\"\"test1\"")
      "Две страницы загружаются успешно.")))
