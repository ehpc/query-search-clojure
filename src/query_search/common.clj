(ns query-search.common
  "Модуль общих функций."
  (:import [java.util Date UUID]
           [java.text SimpleDateFormat]))

(defn get-datetime
  "Возвращает дату и время с миллисекундами."
  []
  (.format (SimpleDateFormat. "dd.MM.YYYY HH:mm:ss.SSS") (Date.)))

(defn generate-uuid
  "Возвращает уникальный идентификатор"
  []
  (str (UUID/randomUUID)))

(defn string-to-int
  "Преобразует строку в число."
  [x]
  (cond (= x "") 0
        (string? x) (Integer/parseInt x)
        :else x))
