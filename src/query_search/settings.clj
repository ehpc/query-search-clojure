(ns query-search.settings
  "Модуль для работы с настройками приложения."
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

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
