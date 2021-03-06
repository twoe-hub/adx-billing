(ns adx-billing.middleware
  (:require
   [adx-billing.env :refer [defaults]]
   [adx-billing.config :refer [env]]
   [adx-billing.layout :refer [error-page]]
   ;; [adx-billing.middleware.formats :as formats]
   [adx-billing.middleware.access-rules :refer [wrap-authr]]
   [adx-billing.msg.bundle :refer [msg]]
   [clojure.tools.logging :as log]
   ;; [muuntaja.middleware :refer [wrap-format wrap-params]]
   [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
   [ring.middleware.flash :refer [wrap-flash]]
   [ring.middleware.session :refer [wrap-session]]
   [ring.middleware.format :refer [wrap-restful-format]]
   [ring-ttl-session.core :refer [ttl-memory-store]]))

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error t (.getMessage t))
        (error-page {:status 500
                     :title (msg :error.very-bad/title)
                     :message (msg :error.very-bad/msg)})))))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
    handler
    {:error-response
     (error-page
       {:status 403
        :title (msg :validation/invalid-token)})}))


;; (defn wrap-formats [handler]
;;   (let [wrapped (-> handler wrap-params (wrap-format formats/instance))]
;;     (fn [request]
;;       ;; disable wrap-formats for websockets
;;       ;; since they're not compatible with this middleware
;;       ((if (:websocket? request) handler wrapped) request))))

(defn wrap-formats [handler]
  (let [wrapped (wrap-restful-format
                 handler
                 {:formats [:json-kw :transit-json :transit-msgpack]})]
    (fn [request]
      ;; disable wrap-formats for websockets
      ;; since they're not compatible with this middleware
      ((if (:websocket? request) handler wrapped) request))))

(defn wrap-base [handler]
  (->
   ((:middleware defaults) handler)
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)
            ;; (assoc-in  [:session :store] (ttl-memory-store (* 60 30)))
            (dissoc :session)
            ))
      wrap-authr
      wrap-flash
      wrap-session
      wrap-internal-error))
