(defproject adx-billing "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[camel-snake-kebab "0.4.1"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [cheshire "5.9.0"]
                 [clj-http/clj-http "3.10.0"]
                 [clojure.java-time "0.3.2"]
                 [conman "0.8.4"]
                 [cprop "0.1.15"]
                 [expound "0.8.3"]
                 [funcool/struct "1.4.0"]
                 [luminus-jetty "0.1.7"]
                 [luminus-migrations "0.6.6"]
                 [luminus/ring-ttl-session "0.3.3"]
                 [markdown-clj "1.10.1"]
                 [metosin/muuntaja "0.6.6"]
                 [metosin/reitit "0.3.10"]
                 [metosin/ring-http-response "0.9.1"]
                 [mount "0.1.16"]
                 [nrepl "0.6.0"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.238" :scope "provided"]
                 [org.clojure/tools.cli "0.4.2"]
                 [org.clojure/tools.logging "0.5.0"]
                 [org.postgresql/postgresql "42.2.9"]
                 [org.webjars.npm/material-icons "0.3.1"]
                 [org.webjars/webjars-locator "0.38"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.8.0"]
                 [ring/ring-defaults "0.3.2"]
                 [buddy/buddy-auth "2.2.0"]
                 [selmer "1.12.18"]
                 [reagent "0.8.1"]
                 [cljs-ajax "0.7.3"]
                 [luminus-transit "0.1.2"]
                 [tick "0.4.23-alpha"]]

  :min-lein-version "2.0.0"
  :source-paths ["src/clj" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources"]
  :target-path "target/%s/"
  :main ^:skip-aot adx-billing.core

  :plugins [[lein-immutant "2.1.0"]
            ;; Too slow
            ;; [yogthos/lein-sass "0.1.10"]
            [lein-scss "0.3.0"]
            ]

  ;; Uncomment the block below, and comment out the next to use yogthos/lein-sass
  ;; :sass {:source "resources/sass" :target "resources/public/css"}
  :scss {:builds
         {:development {:source-dir "resources/sass"
                        :dest-dir "resources/public/css"
                        :executable "sassc"
                        :args ["--style" "expanded"]}
          :production {:source-dir "resources/sass"
                       :dest-dir "resources/public/css"
                       :executable "sassc"
                       :args ["--style" "compressed"]}}}

  :clean-targets ^{:protect false} [...targets...]

  :profiles
  {:uberjar {:omit-source true
             :aot :all
             :uberjar-name "adx-billing.jar"
             :source-paths ["env/prod/clj" "src/cljs" "src/cljc"]
             :resource-paths ["env/prod/resources"]
             }

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-Dconf=dev-config.edn"]
                  :dependencies [[expound "0.7.2"]
                                 [pjstadig/humane-test-output "0.10.0"]
                                 [prone "2019-07-08"]
                                 [ring/ring-devel "1.8.0"]
                                 [ring/ring-mock "0.4.0"]
                                 ;; clojurescript
                                 [org.clojure/clojurescript "1.10.339"]
                                 [com.bhauman/figwheel-main "0.2.3"]
                                 ;; optional but recommended
                                 [com.bhauman/rebel-readline-cljs "0.1.4"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.24.1"]
                                 [jonase/eastwood "0.3.5"]]
                  :source-paths ["env/dev/clj" "src/cljs" "src/cljc"]
                  :resource-paths ["env/dev/resources", "target"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts ["-Dconf=test-config.edn"]
                  :resource-paths ["env/test/resources"]}
   :profiles/dev {}
   :profiles/test {}}

  :aliases {"fig" ["trampoline" "run" "-m" "figwheel.main"]})
