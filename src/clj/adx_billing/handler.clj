(ns adx-billing.handler
  (:require
    [adx-billing.middleware :as middleware]
    [adx-billing.layout :refer [error-page]]
    [adx-billing.routes.auth :refer [auth-routes]]
    [adx-billing.routes.aff :refer [aff-routes]]
    [adx-billing.routes.qutn :refer [qutn-routes]]
    [adx-billing.routes.wo :refer [wo-routes]]
    [adx-billing.routes.inv :refer [inv-routes]]
    [adx-billing.routes.user :refer [user-routes]]
    [adx-billing.routes.profile :refer [profile-routes]]
    [reitit.ring :as ring]
    [ring.middleware.content-type :refer [wrap-content-type]]
    [ring.middleware.webjars :refer [wrap-webjars]]
    [adx-billing.env :refer [defaults]]
    [mount.core :as mount]))

(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

(mount/defstate app-routes
  :start
  (ring/ring-handler
    (ring/router
     [(auth-routes)
      (profile-routes)
      (aff-routes)
      (qutn-routes)
      (wo-routes)
      (inv-routes)
      (user-routes)
      ])
    (ring/routes
      (ring/create-resource-handler
        {:path "/"})
      (wrap-content-type
        (wrap-webjars (constantly nil)))
      (ring/create-default-handler
        {:not-found
         (constantly (error-page {:status 404, :title "404 - Page not found"}))
         :method-not-allowed
         (constantly (error-page {:status 405, :title "405 - Not allowed"}))
         :not-acceptable
         (constantly (error-page {:status 406, :title "406 - Not acceptable"}))}))))

(defn app []
  (middleware/wrap-base #'app-routes))
