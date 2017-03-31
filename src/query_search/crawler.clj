(ns query-search.crawler
  "Модуль для загрузки веб-страниц в многопоточном режиме."
  (:require [clojure.core.async :refer [go go-loop chan <! >! <!! >!! sliding-buffer]]))

(def mock-responses ["  \"args\": {\n    \"param\": \"test1\"\n  }, " "  \"args\": {\n    \"param\": \"test2\"\n  }, "])

(defn crawl
  "Загружает веб-страницу."
  [urls]
  (take (count urls) mock-responses))
