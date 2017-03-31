(ns query-search.core
  "Головной модуль приложения."
  (:gen-class)
  (:require [query-search.settings]
            [query-search.rest :refer [handler]]
            [org.httpkit.server :as server]))

(defn -main
  "Точка входа в приложение."
  [& args]
  (let [settings (query-search.settings/get-settings)] ; Получаем настройки приложения
    (server/run-server handler {:port (:port settings)}))) ; Запускаем веб-сервер
