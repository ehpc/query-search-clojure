(ns query-search.rest
  "Модуль REST API"
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]))

(def search
  "Маршрут API /search."
  (GET "/search" {{query :query} :params} (str "Query:" query)))

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
