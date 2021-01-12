(ns adx-billing.routes.auth
  (:require
   [adx-billing.auth.validate :refer [validate]]
   [adx-billing.html.templates :refer [login-template]]
   [adx-billing.db.core :refer [query]]
   [adx-billing.middleware :as middleware]
   [camel-snake-kebab.core :as csk]
   [camel-snake-kebab.extras :as cske]
   [ring.util.http-response :as response]
   [ring.util.response :refer [redirect]]
   [buddy.hashers :as hashers]
   ))

(def trusted-algs #{:bcrypt+sha512})

(defn- make-tree
  ([coll] (for [x (remove :parent-id coll)]
            {:self x :children (make-tree x coll)}))
  ([root coll]
   (for [x coll :when (= (:parent-id x) (:id root))]
     {:self x :children (make-tree x coll)})))

(defn- get-modules [access]
  (let [coll (cske/transform-keys csk/->kebab-case-keyword
                                  (vec (query :get-modules {:access access})))]
    (make-tree coll)))

(defn- get-access [uid]
  (set (map #(get % :name) (query :get-access {:uid uid}))))

(defn- get-user [username]
  (cske/transform-keys csk/->kebab-case-keyword (query :auth! {:username username})))

(defn auth! [request]
  (let [username (get-in request [:params :username])
        plain-pwd (get-in request [:params :password])
        session (:session request)
        {hashed-pwd :password :as user} (get-user username)]
    (if (hashers/check plain-pwd hashed-pwd)
      (let [next-url (get-in session [:next] "/")
            roles (get-access (:id user))
            updated-session (assoc session
                                   :identity (dissoc user :password)
                                   :roles roles
                                   :menu (get-modules roles))]
        (-> (response/ok {:status :ok :next next-url})
            (assoc :session (dissoc session :next))
            (assoc :session updated-session)))
      (response/internal-server-error
       {:errors {:server-error ["Incorrect username or password!"]}}))))

(defn terms [request]
  (response/content-type
   (response/file-response "resources/html/terms.html")
   "text/html; charset=utf-8")
  )

(defn policy [request]
  (response/content-type
   (response/file-response "resources/html/policy.html")
   "text/html; charset=utf-8"))

(defn login [request]
  (let [next-url (get-in request [:params :next] "/")
        session (:session request)
        upd-sess (assoc session :next next-url)]
    (->
     (response/content-type
      (response/ok
       (login-template {:title "Login | e-Billing"
                        :css ["/css/start.css"
                              "/css/views/login/login.css"]
                        :js ["/js/app/cljs_base.js"
                             "/js/app/adx_billing/auth/login.js"]}))
      "text/html; charset=utf-8")
     (assoc :session upd-sess))))

(defn logout [request]
  (-> (redirect "/auth/login")
      (assoc :session {})))

(defn auth-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/auth/login" {:get login}]
   ["/auth/policy" {:get policy}]
   ["/auth/terms" {:get terms}]
   ["/auth/auth" {:post auth!}]
   ["/logout" {:get logout}]])
