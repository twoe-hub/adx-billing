(ns adx-billing.routes.inv
  (:require
   [adx-billing.db.core :refer [query queries]]
   [adx-billing.inv.validate :refer [validate]]
   [adx-billing.html.templates :refer [base-template]]
   [adx-billing.middleware :as middleware]
   [camel-snake-kebab.core :as csk]
   [camel-snake-kebab.extras :as cske]
   [clojure.string :as cljstr]
   [clojure.pprint :refer [pprint]]
   [conman.core :refer [snip]]
   [ring.util.http-response :as response]))

(defonce sort-cols {:inv-no "i.inv-no" :wo-no "wo.wo-no" :quote-no "q.quote-no" :value "i.value" :cat "c.name" :sub-cat "sc.name" :issued-to "i.issued-to" :issued-by "i.issued-by" :date-issued "i.date-issued"})

(defn save-inv! [{:keys [params]}]
  (if-let [errors (validate params)]
    (response/bad-request {:errors errors})
    (try
      (query :create-user! params)
      (response/ok {:status :ok})
      (catch Exception e
        (response/internal-server-error
         {:errors {:server-error ["Failed to save user!"]}})))))

(defn get-status-counts [params]
  (let [sx (query :count-invs {:cond  (snip queries :cond-invs params)})
        m (into {} (map #(->
                          {(keyword (cljstr/lower-case (% :status))) (% :count)}
                          ) sx))]
    (conj {:all (reduce + (vals m))} m)))

(defn get-order [order]
  (if (> order 0) "asc" "desc"))

(defn parse-params [params]
  (assoc params
         :offset (Integer. (:offset params))
         :limit (Integer. (:limit params))
         :order (get-order (Integer. (:order params)))
         :sort (csk/->snake_case (sort-cols (keyword (:sort params))))))

(defn get-invs [{:keys [params]}]
  (let [params (parse-params params)
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
                (vec (query :get-invs
                            (assoc params
                                   :cond (snip queries :cond-invs params)))))})))

(defn list-invs [request]
  (response/content-type
   (response/ok
    (base-template request {:title "Invoices"
                            :css ["/css/start.css"
                                  "/css/views/inv/inv.css"]
                            :js ["/js/app/cljs_base.js"
                                 "/js/app/adx_billing/inv/inv.js"]}))
   "text/html; charset=utf-8"))

(defn inv-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/inv/list" {:get list-invs}]
   ["/inv/invs" {:get get-invs}]
   ["/inv/save" {:post save-inv!}]])
