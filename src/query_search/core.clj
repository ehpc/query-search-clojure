(ns query-search.core
  "Головной модуль приложения."
  (:gen-class)
  (:require [query-search.settings]
            [query-search.rest :refer [handler]]
            [ring.adapter.jetty :as jetty]))

(defn -main
  "Точка входа в приложение."
  [& args]
  (let [settings (query-search.settings/get-settings)] ; Получаем настройки приложения
    (jetty/run-jetty handler {:port (:port settings)}))) ; Запускаем веб-сервер
