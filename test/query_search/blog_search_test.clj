(ns query-search.blog-search-test
  "Тестирование поиска по блогам."
  (:require [clojure.test :refer :all]
            [clojure.string :refer [includes?]]
            [query-search.testing.fake-server :as server]
            [query-search.blog-search :refer [search]]))


(defn fake-server-fixture
  "Fixture для тестирования API.
   Запускает и останавливает API-сервер."
  [f]
  (let [stop (server/start)]
    (f)
    (stop :timeout 500)))


(deftest search-test
  (testing "Поиск по блогам."
    (is (includes? (first @(search ["scala"])) "yablogs:author"))))


;;; Используем фейковый API-сервер для тестирования
(use-fixtures :once fake-server-fixture)
