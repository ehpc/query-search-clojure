(ns query-search.logger
  "Модуль логирования."
  (:require [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.core :as appenders]))

;;; Настройки логирования
(def log-file-name "log/default.log")
(timbre/merge-config! {:appenders {:println {:enabled? false}}})
(timbre/merge-config! {:appenders {:spit (appenders/spit-appender {:fname log-file-name})}})

(defn- apply-macro
  "Apply для макросов логирования."
  [fn-name args]
  (eval (cons fn-name args)))

(defn log
  "Стандартное логирование."
  [& args]
  (apply-macro 'taoensso.timbre/info args))

(defn spy
  "Логирует возвращая значение."
  [& args]
  (apply-macro 'taoensso.timbre/spy (cons :debug args)))
