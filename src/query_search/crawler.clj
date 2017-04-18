(ns query-search.crawler
  "Модуль для загрузки веб-страниц в многопоточном режиме."
  (:require [org.httpkit.client :as http]
            [clojure.core.async :as async]
            [query-search.misc.common :as common]
            [query-search.misc.logger :refer [log spy]]
            [query-search.settings :as settings]))

;;; Максимальное количество одновременных запросов
(def max-concurrent-requests (settings/get-setting :max-concurrent-requests))

;;; Очередь выполнения
(def queue-channel (async/chan max-concurrent-requests))

;;; Инициализируем свободные слоты в очереди
(dotimes [n max-concurrent-requests] (async/>!! queue-channel true))

(defn execute-request
  "Выполняет запрос, дожидаясь его завершения, чтобы убрать его из очереди."
  [request-fn request-params]
  (spy "Запрос выполняется" request-params)
  (let [result @(request-fn request-params)]
    (async/>!! queue-channel true) ; Освобождаем слот очереди
    (spy "Запрос выполнен" request-params)
    (assoc result :uuid (:uuid request-params))))

(defn queue-request
  "Добавляет запрос в очередь на обработку."
  [request-fn request-params]
  (future
    ;; Занимаем слот в очереди
    (async/<!! queue-channel)
    ;; Добавляем идентификатор и выполняем запрос
    (execute-request request-fn request-params)))

(defn create-request-collection
  "Создаёт коллекцию запросов для постановки в очередь."
  [requests]
  (map
    (fn
      [request]
      (spy "Входящие параметры загрузки:"
        [http/request (hash-map :uuid (common/generate-uuid) :url (:url request) :query-params (:params request))]))
    requests))

(defn crawl
  "Загружает веб-страницы."
  [requests]

  (log "Загружаем веб-страницы:" (apply str requests))
  (future
    (let [n (count requests)
          completion-queue (atom []) ; Коллекция выполненных запросов
          completion-channel (async/chan 1)
          request-collection (create-request-collection requests)]
      (doseq
        [request request-collection]
        (async/go
          ;; Добавляем запрос в очередь выполнения и потом добавляем ответ запроса в коллекцию
          (swap! completion-queue conj @(queue-request (first request) (last request)))
          (when (= (count @completion-queue) n) ; Когда все ответы собраны
            (async/>! completion-channel @completion-queue)))) ; Отдаём ответы
      ;; Ждём, пока все запросы не выполнятся и получаем их ответы
      (let [responses (spy "Все ответы запросов:" (async/<!! completion-channel))
            ;; Сортируем ответы по изначальной очередности запросов
            responses-sorted (apply
                               vector
                               (map
                                 (fn [x] (first (filter ; Сопоставляет запросы и ответы
                                                  #(= (:uuid %) (:uuid (last x)))
                                                  responses)))
                                 request-collection))]
        (spy "Отсортированные ответы запросов:" responses-sorted)
        ;; Возвращаем либо весь ответ, либо только текст ответа
        (map :body responses-sorted)))))
