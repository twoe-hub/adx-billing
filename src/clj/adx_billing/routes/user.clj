(ns adx-billing.routes.user
  (:require
   [adx-billing.db.core :as db]
   [adx-billing.user.validate :refer [validate]]
   [adx-billing.html.templates :refer [base-template]]
   [adx-billing.middleware :as middleware]
   [camel-snake-kebab.core :as csk]
   [camel-snake-kebab.extras :as cske]
   [clojure.pprint :refer [pprint]]
   [ring.util.http-response :as response]
   ))

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
  (let [params (assoc params
                      :offset (Integer. (:offset params))
                      :limit (Integer. (:limit params)))]
    (response/ok
     {:status-counts (let [v1 (db/count-users)
                           v2 (map #(-> (if (:status %)
                                          (assoc % :status 'active)
                                          (assoc % :status 'inactive))) v1)]
                       (conj v2
                             {:status 'all
                              :count (reduce #(-> (+ %1 (:count %2))) 0 v2)}))
      :users (cske/transform-keys csk/->kebab-case-keyword
                                  (vec (db/get-users params)))})))

(defn list-user [request]
  (response/content-type
   (response/ok
    (base-template request {:title "Users"
                            :css ["/css/start.css"
                                  "/css/views/user/user.css"]
                            :js ["/js/app/cljs_base.js"
                                 "/js/app/adx_billing/user/user.js"]}))
   "text/html; charset=utf-8"))

(defn user-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get list-user}]
   ["/user/list" {:get list-user}]
   ["/user/users" {:get get-users}]
   ["/user/save" {:post save-user!}]])
