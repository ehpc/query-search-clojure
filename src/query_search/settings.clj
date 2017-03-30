(ns query-search.settings
  "Модуль для работы с настройками приложения."
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn get-settings
  "Возвращает хэш-таблицу настроек приложения."
  []
  (edn/read-string (slurp (io/resource "settings.edn"))))
