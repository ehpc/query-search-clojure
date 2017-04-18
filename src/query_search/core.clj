(ns query-search.core
  "Головной модуль приложения."
  (:gen-class)
  (:require [org.httpkit.server :as server]
            [query-search.misc.logger :refer [log]]
            [query-search.rest :refer :as rest]
            [query-search.settings :as settings]))

(defn -main
  "Точка входа в приложение."
  [& args]
  (log "Запускаем сервер со следующими настройками:" (settings/get-settings))
  (println "Starting query-search server.")
  (server/run-server rest/handler {:port (settings/get-setting :port)})) ; Запускаем веб-сервер
