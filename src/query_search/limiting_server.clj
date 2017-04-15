(ns query-search.limiting-server
  "Сервер для тестирования ограничений по запросам."
  (:require [org.httpkit.server :refer [run-server with-channel on-close send!]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]))

;;; TODO
(def max-allowed-concurrent-requests 2)

;;; Текущее количество одновременных запросов
(def current-concurrent-requests-count (atom 0))

;;; Ответ сервера при разрешенном запросе
(def success-response {:status 200
                       :headers {"Content-Type" "text/html"}})

;;; Ответ сервера при превышении количества одновременных запросов
(def fail-response {:status 429})


(defn on-channel-close
  "Обрабочик закрытия канала."
  [status]
  ;; Уменьшаем счетчик одновременных запросов
  (swap! current-concurrent-requests-count dec))


(defn limiting-server-handler
  "Обработчик входящих запросов."
  [request]
  ;; Увеличиваем счетчик одновременных запросов
  (swap! current-concurrent-requests-count inc)
  ;; Если счетчик запросов превысил максимально допустимое значение, возвращаем статус ошибки
  (if (> @current-concurrent-requests-count max-allowed-concurrent-requests)
    fail-response
    ;; В противном случае принимаем запрос
    (with-channel request channel
                  (on-close channel on-channel-close)
                  (send! channel (assoc success-response :body (-> request :params :return))))))


(defn start
  "Запуск сервера."
  []
  (run-server (wrap-defaults limiting-server-handler api-defaults) {:port 9197}))
