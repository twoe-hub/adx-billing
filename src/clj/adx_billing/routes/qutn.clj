(ns adx-billing.routes.qutn
  (:require
   [adx-billing.db.core :refer [query queries]]
   [adx-billing.qutn.validate :refer [validate]]
   [adx-billing.html.templates :refer [base-template]]
   [adx-billing.middleware :as middleware]
   [camel-snake-kebab.core :as csk]
   [camel-snake-kebab.extras :as cske]
   [clojure.string :as cljstr]
   [clojure.pprint :refer [pprint]]
   [conman.core :refer [snip]]
   [ring.util.http-response :as response]))

(defn save-qutns! [{:keys [params]}]
  (if-let [errors (validate params)]
    (response/bad-request {:errors errors})
    (try
      (query :create-user! params)
      (response/ok {:status :ok})
      (catch Exception e
        (response/internal-server-error
         {:errors {:server-error ["Failed to save user!"]}})))))

(defn get-status-counts [params]
  (let [sx (query :count-qutns {:cond  (snip queries :cond-qutns params)})
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
         :sort (csk/->snake_case (:sort params))))

(defn get-qutns [{:keys [params]}]
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
                (vec (query :get-qutns
                            (assoc params
                                   :cond (snip queries :cond-qutns params)))))})))

(defn list-qutn [request]
  (response/content-type
   (response/ok
    (base-template request {:title "Quotations"
                            :css ["/css/start.css"
                                  "/css/views/qutn/qutn.css"]
                            :js ["/js/app/cljs_base.js"
                                 "/js/app/adx_billing/qutn/qutn.js"]}))
   "text/html; charset=utf-8"))

(defn qutn-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/qutn/list" {:get list-qutn}]
   ["/qutn/qutns" {:get get-qutns}]
   ["/qutn/save" {:post save-qutns!}]])
