(ns query-search.crawler
  "Модуль для загрузки веб-страниц в многопоточном режиме."
  (:require [clojure.core.async :refer [go go-loop chan <! >! <!! >!! sliding-buffer]]
            [org.httpkit.client :as http]
            [query-search.logger :refer :all]))

(defn crawl
  "Загружает веб-страницы."
  [requests]
  (log "Загружаем веб-страницы:" (apply str requests))
  (future
    (reduce
      #(conj %1 (:body @%2))
      []
      (doall
        (map
          (fn [request] (http/get (:url request) {:query-params (:params request)}))
          requests)))))
