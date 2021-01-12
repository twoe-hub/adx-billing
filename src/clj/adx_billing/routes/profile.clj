(ns adx-billing.routes.profile
  (:require
   [adx-billing.db.core :refer [query]]
   [adx-billing.html.templates :refer [base-template]]
   [adx-billing.middleware :as middleware]
   [camel-snake-kebab.core :as csk]
   [camel-snake-kebab.extras :as cske]
   [clj-http.client :as client]
   [clojure.pprint :refer [pprint]]
   [clojure.walk :as walk]
   [ring.util.http-response :as response]
   ))

(defn get-name [req]
  (let [user (-> req
                 :session
                 :identity
                 )]
    (response/ok
     {:name (str (:first-name user) " " (:last-name user))
      :org (:aff-name user)})))

(defn get-user [{:keys [params]}]
  (response/ok
   {:users (cske/transform-keys csk/->kebab-case-keyword
                                (vec (query :get-users
                                      (assoc params
                                             :offset (Integer. (:offset params))
                                             :limit (Integer. (:limit params))))))}))

(defn show-profile [request]
  (response/content-type
   (response/ok
    (base-template request {:title "Profile | e-Billing"
                            :css ["/css/start.css"
                                  "/css/views/profile/profile.css"]
                            :js ["/js/app/cljs_base.js"
                                 "/js/app/adx_billing/profile/profile.js"]}))
   "text/html; charset=utf-8"))

(defn profile-routes []
  [""
   {:middleware [;;middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/profile/welcome" {:get get-name}]
   ["/profile/view" {:get show-profile}]
   ["/profile/user" {:get get-user}]
   ;;["/profile/save" {:post save-user!}]
   ])
