(ns query-search.blog-search
  "Модуль поиска по блогам."
  (:require [clojure.algo.monads :refer [asks domonad reader-m]]
            [query-search.misc.logger :refer [log]]
            [query-search.crawler :as crawler]))

(defn search
  "Поиск в блогах по ключевым словам."
  [keywords]
  (domonad reader-m [settings (asks :settings)]
    (crawler/crawl
      (map
        #(hash-map
           :url (:api-url settings)
           :params {"text" %
                    "numdoc" (:numdoc settings)})
        keywords))))
