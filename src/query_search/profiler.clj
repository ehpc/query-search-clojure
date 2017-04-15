(ns query-search.profiler
  "Модуль профилирования."
  (:require [taoensso.tufte :as tufte]
            [query-search.common :refer :all]))

(defmacro profile
  "Замеряет время выполнения формы, возвращает миллисекунды."
  [form]
  `(* (:total (:clock (last (tufte/profiled {} ~form)))) 0.000001))

(defn label
  "Выводит метку на экран."
  [message]
  (println (format "\n[%s] %s\n" (get-datetime) message)))
