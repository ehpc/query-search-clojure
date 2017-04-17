(ns query-search.parser
  "Парсер ответа поиска по блогам."
  (:require [clj-xpath.core :as xpath]
            [query-search.logger :refer :all]))

(defn- get-links
  "Достаёт ссылки из ответа."
  [xml]
  (map :text (xpath/$x "/rss/channel/item/link" xml)))

(defn- get-domains
  "Достаёт домены из ссылок."
  [links]
  (map #(vector (re-find #"\w+\.\w+(?=[/?#])" (str % "/")) %) links))

(defn parse
  "Извлекает домены из XML."
  [xml]
  (get-domains (get-links xml)))
