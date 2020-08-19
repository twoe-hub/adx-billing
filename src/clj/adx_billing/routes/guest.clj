(ns adx-billing.routes.guest
  (:require
   [adx-billing.db.core :as db]
   [adx-billing.guest.validate-guest :refer [validate]]
   [adx-billing.layout :as layout]
   [adx-billing.middleware :as middleware]
   [cheshire.core :refer [parse-string]]
   [clj-http.client :as client]
   [ring.util.http-response :as response]
   ))

(defn listing-page [request]
  ;; (let [id-token (-> request
  ;;                    :server-exchange
  ;;                    (.getSecurityContext)
  ;;                    (.getAuthenticatedAccount)
  ;;                    (.getPrincipal)
  ;;                    (.getKeycloakSecurityContext)
  ;;                    (.getIdToken))]
  ;;   (println (.getPreferredUsername id-token))
  ;;   (println (.getEmail id-token))
  ;;   (println (.getEmailVerified id-token))
  ;;   )
  (layout/render
   request
   "guest/list.html" {:tab "list"}))

(defn signing-page [request]
  (layout/render
   request
   "guest/sign.html" {:tab "sign"}))

(defn extract-token []
  (get
   (parse-string (:body
                  (client/post "http://localhost:8080/auth/realms/master/protocol/openid-connect/token"
                               {
                                :form-params {:username "keycloak"
                                              :password "password"
                                              :grant_type "password"
                                              :client_id "admin-cli"}
                                }
                               ))) "access_token")
  )

(defn create-user [params]
  (let [token (extract-token)]
    (client/post "http://localhost:8080/auth/admin/realms/adx-billing/users"
                 {
                  :headers {:authorization (str "Bearer " token)}
                  :content-type :json
                  :form-params {
                                :username "adeel.ansari@ymail.com"
                                :firstName "Adeel"
                                :lastName "Ahmad"
                                :email "adeel.ansari@ymail.com"
                                :emailVerified true
                                :enabled true}
                  })
           )
  )

(defn save-message! [{:keys [params]}]
  (if-let [errors (validate params)]
    (response/bad-request {:errors errors})
    (try
      ;; (create-user params)
      (db/save-message! (assoc params :timestamp (java.util.Date.)))
      (response/ok {:status :ok})
      (catch Exception e
        (response/internal-server-error
         {:errors {:server-error ["Failed to save message!"]}})))))

(defn about-page [request]
  (layout/render
   request "about.html"))

(defn message-list [_]
  (response/ok {:messages (vec (db/get-messages))}))

(defn guest-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get listing-page}]
   ["/guest/list" {:get listing-page}]
   ["/guest/sign" {:get signing-page}]
   ["/messages" {:get message-list}]
   ["/message" {:post save-message!}]
   ["/about" {:get about-page}]])
