(ns query-search.blog-search
  "Модуль поиска по блогам."
  (:require [query-search.crawler :as crawler]
            [query-search.settings :as settings]))

(def api-url (:api-url (settings/get-settings)))

(defn search
  "Поиск в блогах по ключевым словам."
  [keywords]
  (crawler/crawl (map #(hash-map :url api-url :params {"text" %}) keywords)))
