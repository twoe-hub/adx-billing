(ns adx-billing.common.listing
  (:require [ajax.core :refer [GET POST]]
            [reagent.core :as rcore]
            [adx-billing.common.util :as util]))

(defonce pg-size 1)
(defonce total (rcore/atom pg-size))

(defn date-time-handler [m]
  (assoc m
         :date-created (util/parse-date-time (:date-created m))
         :last-login (util/parse-date-time (:last-login m))))

(defn clear [params]
  (reset! params {:username "" :first-name "" :last-name "" :email "" :designation "" :offset (:offset @params) :limit (:limit @params)}))

(defn all-empty? [sx]
  (reduce (fn [x y] (and x y)) (map empty? sx)))

(defn get-records [url params model counts]
  (GET url
       {:headers {"Accept" "application/transit+json"}
        :params @params
        :handler #(do
                    (reset! model (map date-time-handler (:users %)))
                    (reset! counts (:counts %))
                    (reset! total (:total %))
                    (when (all-empty? (vals (dissoc @params :limit :offset)))
                      (util/hide-el (.getElementById js/document "listing-filter")))
                    )}))

(defn prefix [sx]
  (vec (let [sx (seq sx)]
         (if (not= (first sx) 1)
           (if (not= (first sx) 2)
             (conj sx nil 1)
             (conj sx 1))
           sx))))

(defn suffix [sx last-pg]
  (if (not= (last sx) last-pg)
    (if (not= (last sx) (dec last-pg))
      (conj sx nil last-pg)
      (conj sx last-pg))
    sx))

(defn neighbr [curr-pg last-pg]
  (cond
    (== curr-pg 1) (if (< last-pg 3)
                     [curr-pg (inc curr-pg)]
                     [curr-pg (inc curr-pg) (+ curr-pg 2)])
    (== curr-pg last-pg) (if (< last-pg 3)
                      [(dec curr-pg) curr-pg]
                      [(- curr-pg 2) (dec curr-pg) curr-pg])
    :else [(dec curr-pg) curr-pg (inc curr-pg)]))

(defn pages [curr-pg last-pg]
  (cond
    (<= last-pg 1) []
    :else (suffix (prefix (neighbr curr-pg last-pg)) last-pg)))

(defn pagination-ui [url params model counts]
  (let [last-pg (int (Math/ceil (/ @total pg-size)))
        curr-pg (inc (/ (@params :offset) pg-size))
        sx (pages curr-pg last-pg)
        indices (range (count sx))]
    (when (> last-pg 1)
      [:nav.pagination.mr-30 {:role "navigation", :aria-label "pagination"}
       [:a
        {:class (str "pagination-previous" (when (= curr-pg 1) " disabled"))
         :on-click #(do
                      (swap! params assoc :offset (- (get @params :offset) pg-size))
                      (get-records url params model counts))}
        [:span.icon.is-small
         [:i.fa.fa-chevron-left]]]
       [:a
        {:class (str "pagination-next" (when (= curr-pg last-pg ) " disabled"))
         :on-click #(do
                      (swap! params assoc :offset (+ (get @params :offset) pg-size))
                      (get-records url params model counts))}
        [:span.icon.is-small
         [:i.fa.fa-chevron-right]]]
       [:ul.pagination-list {:style {:list-style "none"}}
        (doall
         (for [m (zipmap indices sx)]
           (if (nil? (val m))
             [:li {:key (key m)}
              [:span.pagination-ellipsis "…"]]
             [:li {:key (key m)}
              [:a.pagination-link
               {:class (if (= (val m) (inc (/ (get @params :offset) pg-size)))
                         "is-current"
                         "")
                :aria-label (str "Goto page " (val m))
                :aria-current (str (val m))
                :on-click #(do
                             (swap! params assoc :offset (* (dec (val m)) pg-size))
                             (get-records url params model counts))}
               (str (val m))]])))]])))
