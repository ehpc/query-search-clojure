(ns query-search.rest
  "Модуль REST API"
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [org.httpkit.server :as server]
            [clojure.core.async :refer [go]]
            []))

(defn process-search
  "Обработчик поискового запроса."
  [{{query :query} :params} channel]
  (go (server/send! channel query)))

(def search
  "Маршрут API /search."
  (GET "/search" request (server/with-channel request channel (process-search request channel))))

(def error
  "Маршрут 404."
  (route/not-found "Неверный запрос."))

(defroutes rest-routes
  "Задаёт маршруты."
  search
  error)

(def handler
  "Головной обработчик запросов."
  (wrap-defaults rest-routes api-defaults))
