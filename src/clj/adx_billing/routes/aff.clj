(ns adx-billing.routes.aff
  (:require
   [adx-billing.db.core :refer [query]]
   [adx-billing.aff.validate :refer [validate]]
   [adx-billing.html.templates :refer [base-template]]
   [adx-billing.middleware :as middleware]
   [camel-snake-kebab.core :as csk]
   [camel-snake-kebab.extras :as cske]
   [clojure.pprint :refer [pprint]]
   [ring.util.http-response :as response]
   ))

;; (defn save-aff! [{:keys [params]}]
;;   (if-let [errors (validate params)]
;;     (response/bad-request {:errors errors})
;;     (try
;;       (query :create-user! params)
;;       (response/ok {:status :ok})
;;       (catch Exception e
;;         (response/internal-server-error
;;          {:errors {:server-error ["Failed to save user!"]}})))))

(defn get-status-counts [params]
  (let [sx (query :count-affs params)
        m (into {} (map #(-> (cond
                               (= (:status %) "ACTIVE") {:active (:count %)}
                               (= (:status %) "INACTIVE") {:inactive (:count %)})) sx))
        m (if (contains? m :inactive) m (conj m {:inactive 0}))]
    (conj {:all (reduce + (vals m))} m)))

(defn get-order [order]
  (if (> order 0) "asc" "desc"))

(defn parse-params [params]
  (assoc params
         :offset (Integer. (:offset params))
         :limit (Integer. (:limit params))
         :order (get-order (Integer. (:order params)))
         :sort (csk/->snake_case (:sort params))))

(defn get-affs [{:keys [params]}]
  (let [params (parse-params params)
        m (get-status-counts params)]
    (response/ok
     {:counts m
      :total (if (nil? (:enabled params))
               (:all m)
               (if (= (:enabled params) "true")
                 (:active m)
                 (:inactive m)))
      :records (cske/transform-keys csk/->kebab-case-keyword
                                    (vec (query :get-affs params)))})))

(defn list-aff [request]
  (response/content-type
   (response/ok
    (base-template request {:title "Affiliates"
                            :css ["/css/start.css"
                                  "/css/views/aff/aff.css"]
                            :js ["/js/app/cljs_base.js"
                                 "/js/app/adx_billing/aff/aff.js"]}))
   "text/html; charset=utf-8"))

(defn aff-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get list-aff}]
   ["/aff/list" {:get list-aff}]
   ["/aff/affs" {:get get-affs}]
   ;; ["/aff/save" {:post save-aff!}]
   ])
