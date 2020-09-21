(ns adx-billing.middleware
  (:require
   [adx-billing.env :refer [defaults]]
   [adx-billing.config :refer [env]]
   [adx-billing.layout :refer [error-page]]
   [adx-billing.middleware.formats :as formats]

   [cheshire.generate :as cheshire]
   [clojure.java.io :as io]
   [clojure.tools.logging :as log]
   [cognitect.transit :as transit]

   [muuntaja.middleware :refer [wrap-format wrap-params]]

   [ring.util.http-response :refer [forbidden unauthorized]]
   [ring.util.response :refer [redirect]]
   [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
   [ring.middleware.flash :refer [wrap-flash]]
   [ring.middleware.session :refer [wrap-session]]
   [ring-ttl-session.core :refer [ttl-memory-store]]

   [buddy.auth :refer [authenticated? throw-unauthorized]]
   [buddy.auth.accessrules :refer [wrap-access-rules]]
   [buddy.auth.backends :as backends]
   [buddy.auth.backends.session :refer [session-backend]]
   [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]))

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error t (.getMessage t))
        (error-page {:status 500
                     :title "Something very bad has happened!"
                     :message "We've dispatched a team of highly trained gnomes to take care of the problem."})))))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
    handler
    {:error-response
     (error-page
       {:status 403
        :title "Invalid anti-forgery token"})}))


(defn wrap-formats [handler]
  (let [wrapped (-> handler wrap-params (wrap-format formats/instance))]
    (fn [request]
      ;; disable wrap-formats for websockets
      ;; since they're not compatible with this middleware
      ((if (:websocket? request) handler wrapped) request))))

(defn permit-all [request]
  true)

(defn authenticated-access [request]
  (authenticated? (:session request)))

(defn on-error [request val]
  (let [current-url (:uri request)]
    (redirect (format "/auth/login?next=%s" current-url))))

(defn handle-unauthorized [request metadata]
  (if (authenticated? request)
    (forbidden {})
    (let [current-url (:uri request)]
      (redirect (format "/auth/login?next=%s" current-url))
      )))

(defn wrap-auth [handler]
  (let [backend (backends/session {:unauthorized-handler handle-unauthorized})]
    (-> handler
        (wrap-authentication backend)
        (wrap-authorization backend)
        (wrap-access-rules {:rules [{:pattern #"^/favicon.ico$"
                                     :handler permit-all}
                                    {:pattern #"^/assets/.*"
                                     :handler permit-all}
                                    {:pattern #"^/css/.*"
                                     :handler permit-all}
                                    {:pattern #"^/fonts/.*"
                                     :handler permit-all}
                                    {:pattern #"^/js/.*"
                                     :handler permit-all}
                                    {:pattern #"^/auth/.*"
                                     :handler permit-all}
                                    {:pattern #"^/.*"
                                     :handler authenticated-access}
                                    ]
                            :on-error on-error})))
  )

(defn wrap-base [handler]
  (->
   ((:middleware defaults) handler)
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)
            ;; (assoc-in  [:session :store] (ttl-memory-store (* 60 30)))
            (dissoc :session)
            ))
      wrap-auth
      wrap-flash
      wrap-session
      wrap-internal-error))
