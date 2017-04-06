(ns query-search.crawler-test
  "Тестирование загрузчика веб-страниц."
  (:require [clojure.test :refer :all]
            [clojure.string :refer [includes?]]
            [query-search.crawler :refer :all]
            [query-search.settings :as settings]
            [query-search.profiler :refer :all]))

(def request1 {:url "http://httpbin.org/get" :params {"param" "test1"}})
(def request2 {:url "http://httpbin.org/get" :params {"param" "test2"}})
(def request3 {:url "http://httpbin.org/get" :params {"param" "test3"}})
(def request-ru {:url "http://httpbin.org/get" :params {"param" "русские-буквы"}})
(def fake-delay 1000)
(def fake-request1 {:fake true :delay fake-delay})
(def fake-request2 {:fake true :delay 2000})
(def fake-request3 {:fake true :delay 500})

(deftest crawl-test
  (testing "Очередь выполнения на фиктивных запросах."
    (let [n (inc (* max-concurrent-requests 2)) ; Количество запросов в пуле
          requests (map #(assoc % :id (rand-int 10000)) (take n (repeat fake-request3))) ; Фиктивные запросы
          responses @(crawl requests)]
      (is (= @max-concurrent-requests-count max-concurrent-requests)))
    (let [n (inc (* max-concurrent-requests 2)) ; Количество запросов в пуле
          requests (map #(assoc % :id (rand-int 10000)) (take n (repeat fake-request1))) ; Фиктивные запросы
          responses @(crawl requests :full-response? true)
          t1 (profile @(crawl requests))
          t1-min (/ (* n fake-delay) max-concurrent-requests) ; Минмальное ожидаемое время отработки
          t1-max (+ t1-min fake-delay 400)] ; Максимальное ожидаемое время отработки
      (is (< t1-min t1 t1-max) "Одинаковые по времени запросы выполняются нужное количество времени."))
    (let [n (inc (* max-concurrent-requests 2)) ; Количество запросов в пуле
          requests (map #(assoc % :id (rand-int 10000)) [fake-request2 fake-request3 fake-request3]) ; Фиктивные запросы
          responses @(crawl requests :full-response? true)
          request-ids (apply str (map #(str (:id %) ",") requests))
          response-ids (apply str (map #(str (:id (:body %)) ",") responses))]
      (is (= request-ids response-ids) "Ответы приходят в том же порядке, в котором уходят запросы.")))
  (testing "Очередь выполнения на реальных запросах."
    (is
      (=
        (reduce
          #(str %1 (re-find #"\"test\d\"" %2))
          ""
          @(crawl [request1 request2 request3 request2 request1]))
        "\"test1\"\"test2\"\"test3\"\"test2\"\"test1\"")
      "Пять страниц загружаются успешно и ответ приходит в правильном порядке."))
  (testing "Загрузка веб-страницы."
    (is
      (includes?
        (first
          @(crawl [request1])) "\"test1\"")
      "Одна страница загружается успешно.")
    (is
      (=
        (reduce
          #(str %1 (re-find #"\"test\d\"" %2))
          ""
          @(crawl [request1 request2]))
        "\"test1\"\"test2\"")
      "Две страницы загружаются успешно.")
    (is
      (=
        (reduce
          #(str %1 (re-find #"\"test\d\"" %2))
          ""
          @(crawl [request1 request2 request3]))
        "\"test1\"\"test2\"\"test3\"")
      "Три страницы загружаются успешно.")
    (is
      (includes?
        (first
          @(crawl [request-ru])) "\"param\"")
      "Кириллический запрос загружается успешно.")))
