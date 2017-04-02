(ns query-search.stat
  "Модуль сбора статистики по используемым доменам в блогах."
  (:require [query-search.blog-search :as blog-search]
            [query-search.parser :as parser]
            [query-search.logger :refer :all]))

(defn- extract-stats
  "Формирует статистику по предоставленныму списку доменов.
   При этом исключает повторяющиеся ссылки."
  [domain-list]
  (log "Формируем статистику для доменов: [" (apply str domain-list) "]")
  (spy "Частотная таблица по доменам:"
       (frequencies
         (vals
           (into (hash-map)
                 (map (fn [[k v]] (hash-map v k)) domain-list))))))

(defn get-domain-stats-for-blogs
  "Возвращает статистику доменов для блогов по ключевым словам"
  ([keywords]
   (log "Получаем статистику для" keywords)
   (future (extract-stats (apply concat (map parser/parse @(blog-search/search keywords))))))
  ;; Для тестирования
  ([keywords fake-xml]
   (log "Получаем mock-статистику для" keywords)
   (future (extract-stats (apply concat (map parser/parse fake-xml))))))
