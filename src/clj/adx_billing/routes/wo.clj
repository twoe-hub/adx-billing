(ns adx-billing.routes.wo
  (:require
   [adx-billing.db.core :refer [query queries]]
   [adx-billing.wo.validate :refer [validate]]
   [adx-billing.html.templates :refer [base-template]]
   [adx-billing.middleware :as middleware]
   [camel-snake-kebab.core :as csk]
   [camel-snake-kebab.extras :as cske]
   [clojure.string :as cljstr]
   [clojure.pprint :refer [pprint]]
   [conman.core :refer [snip]]
   [ring.util.http-response :as response]))

(defonce sort-cols {:wo-no "wo.wo-no" :quote-no "q.quote-no" :value "wo.value" :cat "c.name" :sub-cat "sc.name" :issued-to "wo.issued-to" :issued-by "wo.issued-by" :date-issued "wo.date-issued"})

(defn save-wo! [{:keys [params]}]
  (if-let [errors (validate params)]
    (response/bad-request {:errors errors})
    (try
      (query :create-user! params)
      (response/ok {:status :ok})
      (catch Exception e
        (response/internal-server-error
         {:errors {:server-error ["Failed to save user!"]}})))))

(defn get-status-counts [params]
  (let [sx (query :count-wos {:cond  (snip queries :cond-wos params)})
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

(defn get-wos [{:keys [params]}]
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
                (vec (query :get-wos
                            (assoc params
                                   :cond (snip queries :cond-wos params)))))})))

(defn list-wos [request]
  (response/content-type
   (response/ok
    (base-template request {:title "Work Orders"
                            :css ["/css/start.css"
                                  "/css/views/wo/wo.css"]
                            :js ["/js/app/cljs_base.js"
                                 "/js/app/adx_billing/wo/wo.js"]}))
   "text/html; charset=utf-8"))

(defn wo-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/wo/list" {:get list-wos}]
   ["/wo/wos" {:get get-wos}]
   ["/wo/save" {:post save-wo!}]])
