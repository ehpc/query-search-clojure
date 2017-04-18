(ns query-search.rest
  "Модуль REST API"
  (:require [cheshire.core :as cheshire]
            [compojure.core :as compojure]
            [compojure.route :as route]
            [org.httpkit.server :as server]
            [ring.logger :as logger]
            [ring.middleware.defaults :as middleware]
            [clojure.core.async :as async]
            [clojure.string :refer [blank?]]
            [query-search.misc.logger :refer [log spy]]
            [query-search.stat :as stat]))

(defn process-search
  "Обработчик поискового запроса."
  [{{query :query} :params} channel]
  (log "Обрабатываем запрос /search:" query)
  (async/go
    (let [stats (try
                  @(stat/get-domain-stats-for-blogs
                     (vec (filter (complement blank?) (if (vector? query) query (vector query)))))
                  (catch Exception e (log (.getMessage e)) {}))]
      (log "Полученная статистика:" stats)
      (server/send! channel (cheshire/generate-string stats {:pretty true})))))

(def search
  "Маршрут API /search."
  (compojure/GET "/search" request (server/with-channel request channel (process-search request channel))))

(def error
  "Маршрут 404."
  (route/not-found "Неверный запрос."))

(compojure/defroutes rest-routes
  "Задаёт маршруты."
  search
  error)

(def handler
  "Головной обработчик запросов."
  (logger/wrap-with-logger (middleware/wrap-defaults rest-routes middleware/api-defaults)))
