(ns query-search.rest-test
  "Тестирование REST-сервиса."
  (:require [cheshire.core :as cheshire]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [org.httpkit.client :as http]
            [org.httpkit.server :as server]
            [query-search.crawler :as crawler]
            [query-search.rest :as rest]
            [query-search.settings :as settings]
            [query-search.testing.fixtures :as fixtures]))


;;; Запрос на REST-сервер
(def rest-server-request {:url (str "http://localhost:" (settings/get-setting :port) "/search")
                          :query-params {"query" "test"}})


(deftest rest-test
  (let [testing-server-port (:testing-server-port settings/settings)
        testing-server-url (str "http://localhost:" testing-server-port "/")
        env {:settings (assoc settings/settings :api-url testing-server-url)}
        stop (server/run-server rest/handler {:port (settings/get-setting :port)})]
    (testing "Проверяем, что REST-сервис возвращает нужное количество доменов в статистике."
      (is (= 20
             (->> (http/request rest-server-request)
                  (repeat 2)
                  (mapcat (comp vals cheshire/parse-string :body deref))
                  (apply +))))
      (try (stop :timeout 500) (catch Exception e)))))


;;; Используем фейковый API-сервер для тестирования
(use-fixtures :once fixtures/fake-server-fixture)
