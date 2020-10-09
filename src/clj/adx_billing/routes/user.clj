(ns adx-billing.routes.user
  (:require
   [adx-billing.db.core :as db]
   [adx-billing.user.validate-user :refer [validate]]
   [adx-billing.html.templates :refer [base-template]]
   [adx-billing.layout :as layout]
   [adx-billing.middleware :as middleware]
   [camel-snake-kebab.core :as csk]
   [camel-snake-kebab.extras :as cske]
   [cheshire.core :refer [parse-string]]
   [clj-http.client :as client]
   [clojure.pprint :refer [pprint]]
   [clojure.string :as string]
   [clojure.walk :as walk]
   [ring.util.http-response :as response]
   ))

(defn- transform-keys
  "Recursively transforms all map keys from strings to keywords."
  [m]
  (let [f (fn [[kw v]]
            (let [k (str kw)]
              (if (re-find #"_" k)
                [(keyword (string/replace (string/replace k "_" "-") ":" "")) v]
                [kw v])))]
    ;; only apply to maps
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn save-user! [{:keys [params]}]
  (if-let [errors (validate params)]
    (response/bad-request {:errors errors})
    (try
      (db/create-user! params)
      (response/ok {:status :ok})
      (catch Exception e
        (response/internal-server-error
         {:errors {:server-error ["Failed to save user!"]}})))))

(defn get-users [{:keys [params]}]
  (response/ok
   {:total (db/count-users)
    :users (cske/transform-keys csk/->kebab-case-keyword
                                (vec (db/get-users
                                      (assoc params
                                             :offset (Integer. (:offset params))
                                             :limit (Integer. (:limit params))))))}))

(defn user-list [request]
  (response/content-type
   (response/ok
    (base-template request {:title "Users | e-Billing"
                            :css ["/css/start.css"
                                  "/css/views/user/user.css"]
                            :js ["/js/app/cljs_base.js"
                                 "/js/app/adx_billing/user/user.js"]}))
   "text/html; charset=utf-8"))

(defn user-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get user-list}]
   ["/user/list" {:get user-list}]
   ["/user/users" {:get get-users}]
   ["/user/save" {:post save-user!}]])
