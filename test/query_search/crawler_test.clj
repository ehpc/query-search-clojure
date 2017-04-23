(ns query-search.crawler-test
  "Тестирование загрузчика веб-страниц."
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [org.httpkit.client :as http]
            [query-search.crawler :as crawler]
            [query-search.misc.common :as common]
            [query-search.settings :as settings]
            [query-search.testing.fixtures :as fixtures]))

(def max-concurrent-requests (settings/get-setting :max-concurrent-requests))

;;; Запрос на ограничивающий сервер
(def limiting-server-request {:url "http://localhost:4242/"
                              :params {"return" 1
                                       "max-concurrent-requests" max-concurrent-requests}})


(defn aggregate-limiting-server-responses
  "Собирает ответы ограничивающего сервера в один."
  [acc x]
  (+ (common/string-to-int acc) (common/string-to-int x)))


(defn responses-to-int
  "Агрегирует все ответы."
  [responses]
  (reduce aggregate-limiting-server-responses 0 responses))


(deftest crawl-test
  (testing "Один запрос отрабатывает успешно."
    (is (= 1 (responses-to-int @(crawler/crawl [limiting-server-request])))))
  (testing "Запрос возвращает правильные данные."
    (is (= 42 (responses-to-int @(crawler/crawl [(assoc-in limiting-server-request [:params "return"] 42)])))))
  (testing "Множество запрсов возвращают правильные данные."
    (is (= 60 (responses-to-int @(crawler/crawl [(assoc-in limiting-server-request [:params "return"] 11)
                                                 (assoc-in limiting-server-request [:params "return"] 7)
                                                 (assoc-in limiting-server-request [:params "return"] 42)])))))
  (testing "Максимальное количество одноременных запросов отрабатывает успешно."
    (is (= max-concurrent-requests
           (responses-to-int @(crawler/crawl (repeat max-concurrent-requests limiting-server-request))))))
  (testing "Если число запросов превысило максимальное на 1, то все они отрабатывают успешно."
    (is (= (inc max-concurrent-requests)
           (responses-to-int @(crawler/crawl (repeat (inc max-concurrent-requests) limiting-server-request))))))
  (testing "Если число запросов превысило максимальное на 3, то все они отрабатывают успешно."
    (is (= (+ max-concurrent-requests 3)
           (responses-to-int @(crawler/crawl (repeat (+ max-concurrent-requests 3) limiting-server-request))))))
  (testing "Если сервер поддерживает N одновременных запросов, а клиент отправляет N + 1, то успешно отработают N запросов."
    (is (= (dec max-concurrent-requests)
           (responses-to-int @(crawler/crawl (->> (dec max-concurrent-requests)
                                                  (assoc-in limiting-server-request [:params "max-concurrent-requests"])
                                                  (repeat max-concurrent-requests))))))))

;;; Используем ограничивающий сервер для тестирования
(use-fixtures :once fixtures/limiting-server-fixture)
