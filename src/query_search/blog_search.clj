(ns query-search.blog-search
  "Модуль поиска по блогам."
  (:require [query-search.crawler :as crawler]
            [query-search.settings :as settings]
            [query-search.logger :refer :all]))

(defn search
  "Поиск в блогах по ключевым словам."
  [keywords]
  (log "Ищем в блогах по ключевым словам:" keywords)
  (crawler/crawl
    (map
      #(spy
         "Сформировали запрос для поиска по блогам:"
         (hash-map
           :url (settings/get-setting "api-url")
           :params {"text" %
                    "numdoc" (settings/get-setting "numdoc")}))
      keywords)))
