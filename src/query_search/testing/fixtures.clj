(ns query-search.testing.fixtures
  (:require [query-search.testing.fake-server :as fake-server]
            [query-search.testing.limiting-server :as limiting-server]))


(defn fake-server-fixture
  "Fixture для тестирования API.
   Запускает и останавливает API-сервер."
  [f]
  (let [stop (fake-server/start)]
    (f)
    (try (stop :timeout 500) (catch Exception e)))) ; Иногда вылетает из-за багнутого HttpServer.stop в http-kit


(defn limiting-server-fixture
  "Fixture для тестирования одновременных запросов к серверу.
   Запускает и останавливает ограничивающий сервер."
  [f]
  (let [stop (limiting-server/start)]
    (f)
    (try (stop :timeout 500) (catch Exception e))))
