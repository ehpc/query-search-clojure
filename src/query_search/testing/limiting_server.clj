(ns query-search.testing.limiting-server
  "Сервер для тестирования ограничений по запросам."
  (:require [org.httpkit.server :as server]
            [ring.middleware.defaults :as middleware]
            [query-search.misc.common :as common]
            [query-search.settings :as settings]))

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
  (if (> @current-concurrent-requests-count (-> request :params :max-concurrent-requests common/string-to-int))
    fail-response
    ;; В противном случае принимаем запрос
    (server/with-channel request channel
                  (server/on-close channel on-channel-close)
                  (Thread/sleep 200) ; Задержка, чтобы пул запросов не очищался слишком быстро
                  (server/send! channel (assoc success-response :body (-> request :params :return))))))


(defn start
  "Запуск сервера."
  []
  (server/run-server (middleware/wrap-defaults server-handler middleware/api-defaults) {:port (settings/get-setting :testing-server-port)}))
