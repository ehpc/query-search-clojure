(ns query-search.crawler
  "Модуль для загрузки веб-страниц в многопоточном режиме."
  (:require [clojure.core.async :refer [go chan <! >! <!! >!!]]
            [org.httpkit.client :as http]
            [query-search.logger :refer :all]
            [query-search.settings :as settings]
            [query-search.profiler :refer :all]))

;;; Максимальное количество одновременных запросов
(def max-concurrent-requests (settings/get-setting "max-concurrent-requests"))

;;; Очередь выполнения
(def queue-channel (chan max-concurrent-requests))

;;; Инициализируем свободные слоты в очереди
(dotimes [n max-concurrent-requests] (>!! queue-channel true))

(defn fake-request
  "Подставной запрос для тестирования."
  [params]
  (future
    (Thread/sleep (:delay params))
    {:body params}))

(defn execute-request
  "Выполняет запрос, дожидаясь его завершения, чтобы убрать его из очереди."
  [request-fn request-params]
  (spy "Запрос выполняется" request-params)
  (let [result @(request-fn request-params)]
    (>!! queue-channel true) ; Освобождаем слот очереди
    (spy "Запрос выполнен" request-params)
    result))

(defn queue-request
  "Добавляет запрос в очередь на обработку."
  [request-fn request-params]
  (future
    (<!! queue-channel) ; Занимаем слот в очереди
    (execute-request request-fn request-params))) ; Выполняем запрос

(defn create-request-collection
  "Создаёт коллекцию запросов для постановки в очередь."
  [requests]
  (map
    (fn
      [request]
      (spy "Входящие параметры загрузки:"
        (if (:fake request) ; Подставной или нормальный запрос
          [fake-request request]
          [http/request (hash-map :url (:url request) :query-params (:params request))])))
    requests))

(defn crawl
  "Загружает веб-страницы."
  [requests]
  (log "Загружаем веб-страницы:" (apply str requests))
  (future
    (let [n (count requests)
          completion-queue (atom []) ; Коллекция выполненных запросов
          completion-channel (chan 1)
          request-collection (create-request-collection requests)]
      (doseq
        [request request-collection]
        (go
          ;; Добавляем запрос в очередь выполнения и потом добавляем ответ запроса в коллекцию
          (swap! completion-queue conj @(queue-request (first request) (last request)))
          (when (= (count @completion-queue) n) ; Когда все ответы собраны
            (>! completion-channel @completion-queue)))) ; Отдаём ответы
      ;; Ждём, пока все запросы не выполнятся, а затем отдаём их ответы
      (spy "Все ответы запросов:" (<!! completion-channel)))))
