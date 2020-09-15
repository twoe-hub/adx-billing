(ns adx-billing.routes.auth
  (:require
   [adx-billing.auth.validate-login :refer [validate]]
   [adx-billing.layout :as layout]
   [adx-billing.db.core :as db]
   [adx-billing.middleware :as middleware]
   [cheshire.core :refer [parse-string]]
   [clj-http.client :as client]
   [ring.util.http-response :as response]
   [ring.util.response :refer [redirect]]
   ))

(defn render-login [request]
  (layout/render request "auth/login.html"))

(defn logout [request]
  (do
    (-> request
        :server-exchange
        (.getSecurityContext)
        (.logout))
    {:status 302
     :headers {"Location" "/"}}))

(defn auth-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/login" {:get render-login}]
   ["/logout" {:get logout}]])
