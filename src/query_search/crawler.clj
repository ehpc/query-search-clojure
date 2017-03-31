(ns query-search.crawler
  "Модуль для загрузки веб-страниц в многопоточном режиме."
  (:require [clojure.core.async :refer [go go-loop chan <! >! <!! >!! sliding-buffer]]
            [org.httpkit.client :as http]))

(defn crawl
  "Загружает веб-страницы."
  [requests]
  (future
    (reduce
      #(conj %1 (:body @%2))
      []
      (doall
        (map
          (fn [request] (http/get (:url request) {:query-params (:params request)}))
          requests)))))
