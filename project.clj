(defproject query-search "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.3.442"]
                 [http-kit "2.2.0"]
                 [ring/ring-defaults "0.2.3"]
                 [compojure "1.5.2"]
                 [cheshire "5.7.0"]
                 [com.github.kyleburton/clj-xpath "1.4.11"]]
  :main ^:skip-aot query-search.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[javax.servlet/servlet-api "2.5"]]}})

