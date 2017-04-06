(ns query-search.crawler
  "Модуль для загрузки веб-страниц в многопоточном режиме."
  (:require [clojure.core.async :refer [go chan <! >! <!! >!!]]
            [org.httpkit.client :as http]
            [query-search.logger :refer :all]
            [query-search.settings :as settings]
            [query-search.profiler :refer :all]
            [query-search.common :refer :all]))

;;; Максимальное количество одновременных запросов
(def max-concurrent-requests (settings/get-setting "max-concurrent-requests"))

;;; Счётчик максимального количество одновременных запросов за время жизни сервиса.
;;; Используется для тестирования.
(def max-concurrent-requests-count (atom 0))

;;; Счётчик текущего количества одновременных запросов.
;;; Используется для тестирования.
(def current-concurrent-requests-count (atom 0))

;;; Очередь выполнения
(def queue-channel (chan max-concurrent-requests))

;;; Инициализируем свободные слоты в очереди
(dotimes [n max-concurrent-requests] (>!! queue-channel true))

(defn fake-request
  "Подставной запрос для тестирования."
  [params]
  (future
    (Thread/sleep (:delay params))
    {:body params :uuid (:uuid params)}))

(defn execute-request
  "Выполняет запрос, дожидаясь его завершения, чтобы убрать его из очереди."
  [request-fn request-params]
  (spy "Запрос выполняется" request-params)
  (let [result @(request-fn request-params)]
    (>!! queue-channel true) ; Освобождаем слот очереди
    (swap! current-concurrent-requests-count dec) ; Уменьшаем счётчик текущих одновременных запросов
    (spy "Запрос выполнен" request-params)
    (assoc result :uuid (:uuid request-params))))

(defn queue-request
  "Добавляет запрос в очередь на обработку."
  [request-fn request-params]
  (future
    ;; Занимаем слот в очереди
    (<!! queue-channel)
    ;; Увеличиваем счётчик текущих одновременных запросов
    (swap! current-concurrent-requests-count inc)
    ;; Обновляем счётчик максимального количества одновременных запросов
    (swap! max-concurrent-requests-count (fn [x y] (if (> y x) y x)) @current-concurrent-requests-count)
    ;; Добавляем идентификатор и выполняем запрос
    (execute-request request-fn request-params)))

(defn create-request-collection
  "Создаёт коллекцию запросов для постановки в очередь."
  [requests]
  (map
    (fn
      [request]
      (spy "Входящие параметры загрузки:"
        (if (:fake request) ; Подставной или нормальный запрос
          [fake-request (assoc request :uuid (generate-uuid))]
          [http/request (hash-map :uuid (generate-uuid) :url (:url request) :query-params (:params request))])))
    requests))

(defn crawl
  "Загружает веб-страницы."
  [requests & {:keys [full-response?] :or {:full-response? false}}]
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
      ;; Ждём, пока все запросы не выполнятся и получаем их ответы
      (let [responses (spy "Все ответы запросов:" (<!! completion-channel))
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
        (if full-response? responses-sorted (map :body responses-sorted))))))
