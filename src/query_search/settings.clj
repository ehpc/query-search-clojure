(ns query-search.settings
  "Модуль для работы с настройками приложения."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

;;; Получаем хэш-таблицу настроек приложения
(def settings (edn/read-string (slurp (io/resource "settings-default.edn"))))

(defn get-setting
  "Возвращает значение параметра с учётом среды выполенния."
  [name]
  (name settings))

(defn get-settings
  "Возвращает все настройки."
  []
  settings)
