(ns query-search.blog-search-test
  "Тестирование поиска по блогам."
  (:require [clojure.string :refer [includes?]]
            [clojure.test :refer [deftest testing is]]
            [query-search.blog-search :as blog-search]
            [query-search.settings :as settings]
            [query-search.testing.fixtures :as fixtures]))


(deftest search-test
  (testing "Поиск по блогам."
    (let [testing-server-url (str "http://localhost:" (:testing-server-port settings/settings) "/")
          env {:settings (assoc settings/settings :api-url testing-server-url)}]
      (is (includes? (first @((blog-search/search ["scala"]) env)) "yablogs:author")))))


;;; Используем фейковый API-сервер для тестирования
(use-fixtures :once fixtures/fake-server-fixture)
