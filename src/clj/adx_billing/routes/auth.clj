(ns adx-billing.routes.auth
  (:require
   [adx-billing.auth.validate-login :refer [validate]]
   [adx-billing.layout :as layout]
   [adx-billing.html.templates :refer [login-template]]
   [adx-billing.db.core :as db]
   [adx-billing.middleware :as middleware]
   [cheshire.core :refer [parse-string]]
   [clj-http.client :as client]
   [hiccup.page :as page]
   [garden.core :refer [css]]
   [ring.util.http-response :as response]
   [ring.util.response :refer [redirect]]
   [buddy.hashers :as hashers]
   ))

(def trusted-algs #{:bcrypt+sha512})

(defn auth! [request]
  (let [username (get-in request [:params :username])
        plain-pwd(get-in request [:params :password])
        session (:session request)
        hashed-pwd (get-in (first (db/auth! {:username username})) [:password])]
    (if (and hashed-pwd (hashers/verify plain-pwd hashed-pwd {:limit trusted-algs}))
      (let [next-url (get-in session [:next] "/")
            updated-session (assoc session :identity (keyword username))]
        (-> (response/ok {:status :ok :next next-url})
            (assoc :session (dissoc session :next))
            (assoc :session updated-session)))
      (response/internal-server-error
       {:errors {:server-error ["Incorrect username or password!"]}}))))

(defn login-page []
  (login-template {:title "Login | e-Billing"
                   :css ["/css/start.css"
                         "/css/views/login/login.css"]
                   :js ["/js/app/cljs_base.js"
                        "/js/app/adx_billing/auth/login.js"]}))

(defn login [request]
  (let [next-url (get-in request [:params :next] "/")
        session (:session request)
        upd-sess (assoc session :next next-url)]
    (->
     (response/content-type
      (response/ok (login-page))
      "text/html; charset=utf-8")
     (assoc :session upd-sess))))

(defn logout
  [request]
  (-> (redirect "/auth/login")
      (assoc :session {})))

(defn auth-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/auth/login" {:get login}]
   ["/auth/auth" {:post auth!}]
   ["/logout" {:get logout}]]
  )
