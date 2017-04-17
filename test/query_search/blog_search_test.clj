(ns query-search.blog-search-test
  "Тестирование поиска по блогам."
  (:require [clojure.test :refer :all]
            [clojure.string :refer [includes?]]
            [query-search.testing.fixtures :refer [fake-server-fixture]]
            [query-search.blog-search :refer [search]]
            [query-search.settings :refer [settings]]))


(deftest search-test
  (testing "Поиск по блогам."
    (let [testing-server-url (str "http://localhost:" (:testing-server-port settings) "/")
          env {:settings (assoc settings :api-url testing-server-url)}]
      (is (includes? (first @((search ["scala"]) env)) "yablogs:author")))))


;;; Используем фейковый API-сервер для тестирования
(use-fixtures :once fake-server-fixture)
