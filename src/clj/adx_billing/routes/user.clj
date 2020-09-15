(ns adx-billing.routes.user
  (:require
   [adx-billing.db.core :as db]
   [adx-billing.user.validate-user :refer [validate]]
   [adx-billing.layout :as layout]
   [adx-billing.middleware :as middleware]
   [camel-snake-kebab.core :as csk]
   [camel-snake-kebab.extras :as cske]
   [cheshire.core :refer [parse-string]]
   [clj-http.client :as client]
   [clojure.string :as string]
   [clojure.walk :as walk]
   [ring.util.http-response :as response]
   ))

(defn extract-token []
  (let [resp (client/post "http://localhost:8080/auth/realms/master/protocol/openid-connect/token"
                     {:form-params {:username "keycloak"
                                    :password "password"
                                    :grant_type "password"
                                    :client_id "admin-cli"}})]
    (get (parse-string (:body resp)) "access_token")))

(defn create-user [params]
  (let [token (extract-token)]
    (client/post "http://localhost:8080/auth/admin/realms/adx-billing/users"
                 {
                  :headers {:authorization (str "Bearer " token)}
                  :content-type :json
                  :form-params {
                                :username (:username params)
                                :firstName (:first-name params)
                                :lastName (:last-name params)
                                :email (:email params)
                                :emailVerified true
                                :enabled true}})))

(defn save-user! [{:keys [params]}]
  (if-let [errors (validate params)]
    (response/bad-request {:errors errors})
    (try
      (create-user params)
      (db/create-user! params)
      (response/ok {:status :ok})
      (catch Exception e
        (response/internal-server-error
         {:errors {:server-error ["Failed to save user!"]}})))))

(defn render-list [request]
  (layout/render request "user/user-list.html" {:tab "list"}))

(defn render-form [request]
  (layout/render request "user/user-edit-form.html" {:tab "create"}))

(defn transform-keys
  "Recursively transforms all map keys from strings to keywords."
  [m]
  (let [f (fn [[kw v]]
            (let [k (str kw)]
              (if (re-find #"_" k) [(keyword (string/replace (string/replace k "_" "-") ":" "")) v] [kw v]))
            )]
    ;; only apply to maps
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn get-users [{:keys [params]}]
  (response/ok {:total (db/count-users)
                :users
                (cske/transform-keys csk/->kebab-case-keyword
                                     (vec (db/get-users
                                           (assoc params
                                                  :offset (Integer. (:offset params))
                                                  :limit (Integer. (:limit params))))))}))

(defn user-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/user/list" {:get render-list}]
   ["/user/users" {:get get-users}]
   ["/user/save" {:post save-user!}]])
