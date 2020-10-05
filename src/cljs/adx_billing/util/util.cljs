(ns adx-billing.util.util
  (:require [ajax.core :refer [GET POST]]
            [cljs.loader :as loader]
            [clojure.string :as string]
            [reagent.core :as r]
            [tick.alpha.api :as t]
            [tick.locale-en-us]
            [adx-billing.user.validate-user :refer [validate]]))

(defn show-modal [elem]
  (.add (.-classList elem) "is-active")
  (.focus elem))

(defn hide-modal
  ([elem]
   (.remove (.-classList elem) "is-active"))

  ([e id]
   (let [elem (.getElementById js/document id)]
     (when (and (= (.-key e) "Escape") (.contains (.-classList elem) "is-active"))
       (hide-modal elem))))

  ([e id fields errors]
   (let [elem (.getElementById js/document id)]
     (when (and (= (.-key e) "Escape") (.contains (.-classList elem) "is-active"))
       (do
         (reset! fields {})
         (reset! errors nil)
         (hide-modal elem))))))

(defn toggle-modal
  ([id]
   (let [elem (.getElementById js/document id)]
     (if (.contains (.-classList elem) "is-active")
       (hide-modal elem)
       (show-modal elem))))
  ([id fields errors]
   (reset! fields {})
   (reset! errors nil)
   (toggle-modal id)))
