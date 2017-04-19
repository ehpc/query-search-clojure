(ns query-search.stat
  "Модуль сбора статистики по используемым доменам в блогах."
  (:require [clojure.algo.monads :refer [ask domonad reader-m]]
            [query-search.misc.logger :refer [log spy]]
            [query-search.blog-search :as blog-search]
            [query-search.parser :as parser]))

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
  [keywords]
  (domonad reader-m [env (ask)]
    (do
      (log "Получаем статистику для" keywords)
      (if (empty? keywords)
          (future {})
          (future
            (extract-stats
              (apply
                concat
                (map
                  parser/parse
                  @((blog-search/search keywords) env)))))))))
