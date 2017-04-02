(ns query-search.settings
  "Модуль для работы с настройками приложения."
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

;; Получаем хэш-таблицу настроек приложения
(def settings (edn/read-string (slurp (io/resource "settings.edn"))))

(defn- get-env-specific-value
  "Возвращает значение настройки с учётом среды выполнения."
  [name]
  (let [env-specific-value ((keyword (str name "-" (clojure.core/name (:env settings)))) settings)]
    (if (some? env-specific-value)
      env-specific-value
      ((keyword name) settings))))

(defn get-setting
  "Возвращает значение параметра с учётом среды выполенния."
  [name]
  (get-env-specific-value name))

(defn get-settings
  "Возвращает все настройки."
  []
  settings)

;; Приложение запущено в разработческом режиме?
(def dev? (= (get-setting "env") :dev))
