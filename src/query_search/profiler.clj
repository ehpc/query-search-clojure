(ns query-search.profiler
  "Модуль профилирования."
  (:require [taoensso.tufte :as tufte])
  (:import [java.util Date]
           [java.text SimpleDateFormat]))

(defmacro profile
  "Замеряет время выполнения формы, возвращает миллисекунды."
  [form]
  `(* (:total (:clock (last (tufte/profiled {} ~form)))) 0.000001))

(defn label
  [message] ; SimpleDateFormat("MM-dd-yyyy").format(myDate)
  (println (format "\n[%s] %s\n" (.format (SimpleDateFormat. "HH:mm:ss.SSS") (Date.)) message)))
