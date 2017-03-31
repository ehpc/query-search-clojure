(ns query-search.crawler-test
  "Тестирование загрузчика веб-страниц."
  (:require [clojure.test :refer :all]
            [clojure.string :refer [includes?]]
            [query-search.crawler :refer :all]))

(def request1 {:url "http://httpbin.org/get" :params {"param" "test1"}})
(def request2 {:url "http://httpbin.org/get" :params {"param" "test2"}})
(def request3 {:url "http://httpbin.org/get" :params {"param" "test3"}})
(def request-ru {:url "http://httpbin.org/get" :params {"param" "русские-буквы"}})

(deftest crawl-test
  (testing "Загрузка веб-страницы."
    (is
      (includes?
        (first
          @(crawl [request1])) "\"test1\"")
      "Одна страница загружается успешно.")
    (is
      (=
        (reduce
          #(str (re-find #"\"test\d\"" %2) %1)
          ""
          @(crawl [request1 request2]))
        "\"test2\"\"test1\"")
      "Две страницы загружаются успешно.")
    (is
      (=
        (reduce
          #(str (re-find #"\"test\d\"" %2) %1)
          ""
          @(crawl [request1 request2 request3]))
        "\"test3\"\"test2\"\"test1\"")
      "Три страницы загружаются успешно.")
    (is
      (includes?
        (first
          @(crawl [request-ru])) "\"param\"")
      "Кириллический запрос загружается успешно.")))
