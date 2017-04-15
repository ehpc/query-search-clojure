(defproject query-search "0.1.0"
  :description "Domain statistics for Yandex blog search."
  :url "https://github.com/ehpc/query-search-clojure"
  :license {:name "MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.3.442"]
                 [http-kit "2.2.0"]
                 [ring/ring-defaults "0.2.3"]
                 [compojure "1.5.2"]
                 [cheshire "5.7.0"]
                 [com.github.kyleburton/clj-xpath "1.4.11"]
                 [com.taoensso/timbre "4.8.0"]
                 [com.taoensso/tufte "1.1.1"]
                 [ring-logger "0.7.7"]]
  :main ^:skip-aot query-search.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[javax.servlet/servlet-api "2.5"]]}})
