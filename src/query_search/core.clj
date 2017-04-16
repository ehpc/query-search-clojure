(ns query-search.core
  "Головной модуль приложения."
  (:gen-class)
  (:require [query-search.settings :as settings]
            [query-search.rest :refer [handler]]
            [org.httpkit.server :as server]
            [query-search.logger :refer :all]))

(defn -main
  "Точка входа в приложение."
  [& args]
  (log "Запускаем сервер со следующими настройками:" (settings/get-settings))
  (println "Starting query-search server.")
  (server/run-server handler {:port (settings/get-setting :port)})) ; Запускаем веб-сервер
