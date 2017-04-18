(ns query-search.misc.profiler
  "Модуль профилирования."
  (:require [taoensso.tufte :as tufte]
            [query-search.misc.common :as common]))

(defmacro profile
  "Замеряет время выполнения формы, возвращает миллисекунды."
  [form]
  `(* (:total (:clock (last (tufte/profiled {} ~form)))) 0.000001))

(defn label
  "Выводит метку на экран."
  [message]
  (println (format "\n[%s] %s\n" (common/get-datetime) message)))
