(ns query-search.stat-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [query-search.settings :as settings]
            [query-search.stat :as stat]
            [query-search.testing.fake-server :as fake-server]))


(defn get-xmls-for-keywords
  "Возвращает коллекцию фейковых XML-ответов для запросов."
  [keywords]
  (map #(fake-server/get-response-for-query %) keywords))


(deftest stat-test
  "Статистика по доменам из поиска по блогам."
  (let [testing-server-url (str "http://localhost:" (:testing-server-port settings/settings) "/")
        env {:settings (assoc settings/settings :api-url testing-server-url)}]
    (testing "Сценарий: Статистика по одному ключевому слову при первом запуске."
      (is
        (> (get (stat/get-domain-stats-for-blogs (get-xmls-for-keywords ["scala"])) "scala.com" 0) 3)
        "Домен scala.com встречается больше 3 раз."))
    (testing "Сценарий: Статистика по одному ключевому слову при повторном запросе."
      (is
        (> (get (stat/get-domain-stats-for-blogs (get-xmls-for-keywords ["scala"])) "scala.com" 0) 3)
        "Домен scala.com встречается больше 3 раз."))
    (testing "Сценарий: Статистика по другому ключевому слову."
      (is
        (> (get (stat/get-domain-stats-for-blogs (get-xmls-for-keywords ["bdd"])) "bdd.com" 0) 3)
        "Домен bdd.com встречается больше 3 раз."))
    (testing "Сценарий: Статистика по двум запросам сразу."
      (is
        (let [stats (stat/get-domain-stats-for-blogs (get-xmls-for-keywords ["puppy" "kitty"]))]
          (and
            (> (get stats "puppy.com" 0) 3)
            (> (get stats "kitty.com" 0) 3)))
        "Домены puppy.com и kitty.com встречаются больше 3 раз каждый."))
    (testing "Сценарий: Запрос с русскими буквами."
      (is
        (> (get (stat/get-domain-stats-for-blogs (get-xmls-for-keywords ["запрос"])) "vk.com" 0) 1)
        "Домен vk.com встречается больше 1 раза."))
    (testing "Сценарий: Запрос с повторяющимися ссылками в ответе API Yandex"
      (is
        (= (get (stat/get-domain-stats-for-blogs (get-xmls-for-keywords ["repeated"])) "repeated5times.com" 0) 1)
        "Домен repeated5times.com встречается ровно 1 раз."))
    (testing "Адекватная реакция, когда блогов не найдено."
      (is
        (empty? (stat/get-domain-stats-for-blogs (get-xmls-for-keywords ["not-found"])))
        "Список доменов пустой."))))
