(ns query-search.crawler-test
  "Тестирование загрузчика веб-страниц."
  (:require [clojure.test :refer :all]
            [clojure.string :refer [includes?]]
            [query-search.limiting-server :as server]
            [query-search.crawler :refer [crawl]]
            [query-search.settings :as settings]
            [query-search.profiler :refer :all]))

(def max-concurrent-requests (settings/get-setting "max-concurrent-requests"))

;;; Запрос на ограничивающий сервер
(def limiting-server-request {:url "http://localhost:9197/" :params {"return" "1"}})


(defn limiting-server-fixture
  "Fixture для тестирования одновременных запросов к серверу.
   Запускает и останавливает ограничивающий сервер."
  [f]
  (let [stop (server/start)]
    (f)
    (try (stop :timeout 500) (catch Exception e)))) ; Иногда вылетает из-за багнутого HttpServer.stop в http-kit


(defn string-to-int
  "Преобразует строку в число."
  [x]
  (cond (= x "") 0
        (string? x) (Integer/parseInt x)
        :else x))


(defn aggregate-limiting-server-responses
  "Собирает ответы ограничивающего сервера в один."
  [acc x]
  (+ (string-to-int acc) (string-to-int x)))


(use-fixtures :once limiting-server-fixture)


(deftest crawl-test
  (testing "Один запрос отрабатывает успешно."
    (= 1 (reduce aggregate-limiting-server-responses @(crawl [limiting-server-request]))))
  (testing "Запрос возвращает правильные данные."
    (= 42 (reduce aggregate-limiting-server-responses @(crawl [(assoc limiting-server-request :params {"return" 42})]))))
  (testing "Максимальное количество одноременных запросов отрабатывает успешно."
    (= max-concurrent-requests (reduce
                                 aggregate-limiting-server-responses
                                 @(crawl (repeat max-concurrent-requests limiting-server-request)))))
  (testing "Если число запросов (N) превысило максимальное на 1, то успешно отработают только N - 1 запросов."
    (= max-concurrent-requests (reduce
                                 aggregate-limiting-server-responses
                                 @(crawl (repeat (inc max-concurrent-requests) limiting-server-request)))))
  (testing "Если число запросов (N) превысило максимальное на 3, то успешно отработают только N - 3 запросов."
    (= max-concurrent-requests (reduce
                                 aggregate-limiting-server-responses
                                 @(crawl (repeat (+ 3 max-concurrent-requests) limiting-server-request))))))
