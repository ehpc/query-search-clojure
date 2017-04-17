(ns query-search.testing.limiting-server
  "Сервер для тестирования ограничений по запросам."
  (:require [org.httpkit.server :refer [run-server with-channel on-close send!]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [query-search.common :refer [string-to-int]]
            [query-search.settings :refer [get-setting]]))

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


(defn server-handler
  "Обработчик входящих запросов."
  [request]
  ;; Увеличиваем счетчик одновременных запросов
  (swap! current-concurrent-requests-count inc)
  ;; Если счетчик запросов превысил максимально допустимое значение, возвращаем статус ошибки
  (if (> @current-concurrent-requests-count (-> request :params :max-concurrent-requests string-to-int))
    fail-response
    ;; В противном случае принимаем запрос
    (with-channel request channel
                  (on-close channel on-channel-close)
                  (Thread/sleep 200) ; Задержка, чтобы пул запросов не очищался слишком быстро
                  (send! channel (assoc success-response :body (-> request :params :return))))))


(defn start
  "Запуск сервера."
  []
  (run-server (wrap-defaults server-handler api-defaults) {:port (get-setting :testing-server-port)}))
