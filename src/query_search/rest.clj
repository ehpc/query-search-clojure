(ns query-search.rest
  "Модуль REST API"
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.logger :as logger]
            [org.httpkit.server :as server]
            [cheshire.core :refer [generate-string]]
            [clojure.string :refer [blank?]]
            [clojure.core.async :refer [go]]
            [query-search.logger :refer :all]
            [query-search.stat :refer [get-domain-stats-for-blogs]]))

(defn process-search
  "Обработчик поискового запроса."
  [{{query :query} :params} channel]
  (log "Обрабатываем запрос /search:" query)
  (go
    (let [stats (try
                  @(get-domain-stats-for-blogs
                     (vec (filter (complement blank?) (if (vector? query) query (vector query)))))
                  (catch Exception e (log (.getMessage e)) {}))]
      (log "Полученная статистика:" stats)
      (server/send! channel (generate-string stats {:pretty true})))))

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
  (logger/wrap-with-logger (wrap-defaults rest-routes api-defaults)))
