(ns query-search.stat-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [query-search.settings :as settings]
            [query-search.stat :as stat]
            [query-search.testing.fixtures :as fixtures]))


(deftest stat-test
  "Статистика по доменам из поиска по блогам."
  (let [testing-server-url (str "http://localhost:" (:testing-server-port settings/settings) "/")
        env {:settings (assoc settings/settings :api-url testing-server-url)}]
    (testing "Сценарий: Статистика по одному ключевому слову при первом запуске."
      (is
        (> (get @((stat/get-domain-stats-for-blogs ["scala"]) env) "scala.com" 0) 3)
        "Домен scala.com встречается больше 3 раз."))
    (testing "Сценарий: Статистика по одному ключевому слову при повторном запросе."
      (is
        (> (get @((stat/get-domain-stats-for-blogs ["scala"]) env) "scala.com" 0) 3)
        "Домен scala.com встречается больше 3 раз."))
    (testing "Сценарий: Статистика по другому ключевому слову."
      (is
        (> (get @((stat/get-domain-stats-for-blogs ["bdd"]) env) "bdd.com" 0) 3)
        "Домен bdd.com встречается больше 3 раз."))
    (testing "Сценарий: Статистика по двум запросам сразу."
      (is
        (let [stats @((stat/get-domain-stats-for-blogs ["puppy" "kitty"]) env)]
          (and
            (> (get stats "puppy.com" 0) 3)
            (> (get stats "kitty.com" 0) 3)))
        "Домены puppy.com и kitty.com встречаются больше 3 раз каждый."))
    (testing "Сценарий: Запрос с русскими буквами."
      (is
        (> (get @((stat/get-domain-stats-for-blogs ["запрос"]) env) "vk.com" 0) 1)
        "Домен vk.com встречается больше 1 раза."))
    (testing "Сценарий: Запрос с повторяющимися ссылками в ответе API Yandex"
      (is
        (= (get @((stat/get-domain-stats-for-blogs ["repeated"]) env) "repeated5times.com" 0) 1)
        "Домен repeated5times.com встречается ровно 1 раз."))
    (testing "Адекватная реакция, когда блогов не найдено."
      (is
        (empty? @((stat/get-domain-stats-for-blogs ["not-found"]) env))
        "Список доменов пустой."))))


;;; Используем фейковый API-сервер для тестирования
(use-fixtures :once fixtures/fake-server-fixture)
