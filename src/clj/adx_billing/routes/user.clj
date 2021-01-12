(ns adx-billing.routes.user
  (:require
   [adx-billing.db.core :refer [query queries]]
   [adx-billing.user.validate :refer [validate]]
   [adx-billing.html.templates :refer [base-template]]
   [adx-billing.middleware :as middleware]
   [camel-snake-kebab.core :as csk]
   [camel-snake-kebab.extras :as cske]
   [clojure.pprint :refer [pprint]]
   [conman.core :refer [snip]]
   [ring.util.http-response :as response]
   ))

(defn save-user! [{:keys [params]}]
  (if-let [errors (validate params)]
    (response/bad-request {:errors errors})
    (try
      (query :create-user! params)
      (response/ok {:status :ok})
      (catch Exception e
        (response/internal-server-error
         {:errors {:server-error ["Failed to save user!"]}})))))

(defn get-status-counts [params]
  (let [sx (query :count-users {:cond  (snip queries :cond-users params)})
        m (into {} (map #(-> (if (:status %)
                               {:active (:count %)}
                               {:inactive (:count %)})) sx))
        m (if (contains? m :inactive) m (conj m {:inactive 0}))]
    (conj {:all (reduce + (vals m))} m)))

(defn get-users [{:keys [params]}]
  (let [params (assoc params
                      :offset (Integer. (:offset params))
                      :limit (Integer. (:limit params)))
        m (get-status-counts params)]
    (response/ok
     {:counts m
      :total (if (nil? (:enabled params))
               (:all m)
               (if (= (:enabled params) "true")
                 (:active m)
                 (:inactive m)))
      :records (cske/transform-keys
                csk/->kebab-case-keyword
                (vec (query :get-users
                            {:cond (snip queries :cond-users params)
                             :enabled (:enabled params)
                             :offset (:offset params)
                             :limit (:limit params)})))})))

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
   ["/user/list" {:get list-user}]
   ["/user/users" {:get get-users}]
   ["/user/save" {:post save-user!}]])
