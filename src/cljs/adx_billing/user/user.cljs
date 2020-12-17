(ns adx-billing.user.user
  (:require [ajax.core :refer [GET POST]]
            [cljs.loader :as loader]
            [clojure.string :as string]
            [goog.dom :as gdom]
            [reagent.core :as rcore]
            [reagent.dom :as rdom]
            [adx-billing.common.util :as util]
            [adx-billing.common.page-el :as page-el]
            [adx-billing.msg.bundle :refer [msg]]
            [adx-billing.user.validate :refer [validate]]
            [adx-billing.common.util :refer [toggle-el]]))

(defonce pg-size 15)
(defonce current-pg (rcore/atom 1))
(defonce last-pg (rcore/atom 1))

(defonce status-counts (rcore/atom nil))
(defonce users (rcore/atom nil))
(defonce sort-on (rcore/atom nil))
(defonce order-by (rcore/atom nil))

(def cols
  ['id 'no 'username 'first-name 'last-name 'email 'designation 'last-login
   'date-created 'enabled])

(defn show-modal [elem]
  (.add (.-classList elem) "is-active")
  (.focus elem))

(defn hide-modal [elem fields errors]
  (reset! fields {})
  (reset! errors nil)
  (.remove (.-classList elem) "is-active"))

(defn toggle-modal
  ([class fields errors]
   (let [elem (first (array-seq
                      (.getElementsByClassName js/document "edit-modal")))]
     (if (.contains (.-classList elem) "is-active")
       (hide-modal elem fields errors)
       (show-modal elem))))
  ([e class fields errors]
   (when (= (.-key e) "Escape")
     (toggle-modal class fields errors))))

(defn date-time-handler [m]
  (assoc m
         :date-created (util/parse-date-time (:date-created m))
         :last-login (util/parse-date-time (:last-login m))))

(defn clear [params]
  (reset! params {:username "" :first-name "" :last-name "" :email "" :designation "" :offset (:offset @params) :limit (:limit @params)}))

(defn get-users [params]
  ;; (when (and (>= pg 1) (<= pg @last-pg)))
  (GET "/user/users"
       {:headers {"Accept" "application/transit+json"}
        :params @params
        :handler #(do
                    (reset! status-counts (:status-counts %))
                    (reset! users (map date-time-handler (:users %)))

                    ;; (reset! last-pg (int (Math/ceil (/ (:total %) pg-size))))
                    ;; (reset! current-pg pg)
                    )}))

(defn save-user [fields errors]                                        ;
  (if-let [validation-errors (validate @fields)]
    (reset! errors validation-errors)
    (POST "/user/save"
          {:format :json
           :headers
           {"Accept" "application/transit+json"
            "x-csrf-token" (.-value (.getElementById js/document "token"))}
           :params @fields
           :handler #(do
                       (swap! users (fn[n] (conj n @fields)))
                       (toggle-modal "edit-modal" fields errors))
           :error-handler #(do
                             (.log js/console (str %))
                             (reset! errors (get-in % [:response :errors])))})))

(defn prefix [v]
  (vec (let [sq (seq v)]
         (if (not= (first sq) 1)
           (if (not= (first sq) 2)
             (conj sq nil 1)
             (conj sq 1))
           v))))

(defn suffix [v last-pg]
  (if (not= (last v) last-pg)
    (if (not= (last v) (dec last-pg))
      (conj v nil last-pg)
      (conj v last-pg))
     v))

(defn neighbr [pg last-pg]
  (cond
    (== pg 1) (if (< last-pg 3)
                [pg (inc pg)]
                [pg (inc pg) (+ pg 2)])
    (== pg last-pg) (if (< last-pg 3)
                      [(dec pg) pg]
                      [(- pg 2) (dec pg) pg])
    :else [(dec pg) pg (inc pg)])
  )

(defn pages [pg last-pg]
  (cond
    (<= last-pg 1) []
    :else (suffix (prefix (neighbr pg last-pg)) last-pg)
    )
  )

(defn errors-component [errors id]
  (when-let [error (id @errors)]
    [:p.help.is-danger (string/join error)]))

(defn input-el
  "An input element which updates its value on change"
  [id name type class placeholder param-name params]
  [:input {:id id
           :name name
           :type type
           :class class
           :placeholder placeholder
           :value ((keyword param-name) @params)
           :on-change #(swap! params assoc
                              (keyword param-name) (-> % .-target .-value))
           }])

(defn login-form-ui [fields errors]
  (fn []
    [:div.columns.is-multiline.is-centered>div.column
     [errors-component errors :server-error]
     [:form {:method "POST", :action "#"}
      [:div.field.is-horizontal
       [:div.field-label.is-normal
        [:label.label "Username"]]
       [:div.field-body>div.field>div.control
        [:input.input.is-normal
         {:type :text
          :name :username
          :on-change #(swap! fields assoc :username (-> % .-target .-value))
          :placeholder "e.g. adeel@ymail.com"
          :value (:username @fields)}]
        [errors-component errors :username]]]
      [:div.field.is-horizontal
       [:div.field-label.is-normal
        [:label.label "First Name"]]
       [:div.field-body>div.field>div.control
        [:input.input.is-normal
         {:type :text
          :name :first-name
          :on-change #(swap! fields assoc :first-name (-> % .-target .-value))
          :placeholder "e.g. Adeel"
          :value (:first-name @fields)}]
        [errors-component errors :first-name]]]
      [:div.field.is-horizontal
       [:div.field-label.is-normal
        [:label.label "Last Name"]]
       [:div.field-body>div.field>div.control
        [:input.input.is-normal
         {:type :text
          :name :last-name
          :on-change #(swap! fields assoc :last-name (-> % .-target .-value))
          :placeholder "e.g. Ansari"
          :value (:last-name @fields)}]
        [errors-component errors :last-name]]]
      [:div.field.is-horizontal
       [:div.field-label.is-normal
        [:label.label "Email"]]
       [:div.field-body>div.field>div.control
        [:input.input.is-normal
         {:type :text
          :name :email
          :on-change #(swap! fields assoc :email (-> % .-target .-value))
          :placeholder "e.g. adeel@ymail.com"
          :value (:email @fields)}]
        [errors-component errors :email]]]
      ]]))

(defn modal-ui [fields errors]
  (let [modal-id "edit-modal"]
    [:div.modal
     {:id modal-id
      :tab-index "0"
      :on-key-up #(toggle-modal % fields errors)
      :style {:justify-content "flex-start"
              :padding-top "150px"}}
     [:div.modal-background]
     [:div.modal-card
      [:header.modal-card-head
       [:p.modal-card-title ""]
       [:button.delete
        {:aria-label "close"
         :on-click #(toggle-modal modal-id fields errors)}]]
      [:section.modal-card-body [(login-form-ui fields errors)]]
      [:footer.modal-card-foot.is-right.pt-50.pb-20.pr-20
       {:style {:justify-content "right"}}
       [:button.button {:on-click #(toggle-modal modal-id fields errors)}
        "Cancel"]
       [:button.button {:on-click #(save-user fields errors)}
        "Save"]]]]))

(defn pagination-ui [current-pg last-pg]
  (when (> @last-pg 1)
    [:nav.pagination.mr-30 {:role "navigation", :aria-label "pagination"}
     [:a.pagination-previous
      {:on-click #(get-users (dec @current-pg))} "Previous"]
     [:a.pagination-next
      {:on-click #(get-users (inc @current-pg))} "Next page"]
     [:ul.pagination-list {:style {:list-style "none"}}
      (for [pg (pages @current-pg @last-pg)]
        (if (nil? pg)
          [:li
           [:span.pagination-list "…"]]
          [:li
           [:a.pagination-link
            {:class (if (= pg @current-pg) "is-current" "")
             :aria-label "Goto page 1"
             :aria-current (if (= pg @current-pg) "page" "")
             :on-click #(get-users pg)} (str pg)]]))]]))

(defn action-ui [fields errors]
  [:div.field.is-grouped.is-grouped-centered
   [:div.control
    [:button.button {:disabled true} "Delete"]]
   [:div.control
    [:button.button.pl-25.pr-25
     {:on-click #(toggle-modal "edit-modal" fields errors)} "Add"]]])

(defn quick-filter [params]
  [:div#quick-filter.tabs.is-flex
   [:ul
    (map (fn [filter]
           [:li.is-active {:key (:status filter)}
            [:a {:on-click #(get-users 1)}
             (str (msg (keyword (str "user.qf-label/" (:status filter)))) ": " (:count filter))]])
         @status-counts)]])

(defn table-head-row [params]
  [:tr
   (doall (map (fn [col]
                 (if (= col 'id)
                   [:th
                    {:key col}
                    [:div.field
                     [:div.control
                      [:label.checkbox
                       [:input.table-checkbox-all {:id "checkall"
                                                   :type "checkbox"
                                                   :name "checkall"}]]]]]
                   [:th.is-sortable
                    {:key col} (msg (keyword (str "user.cols/" (name col))))]))
               cols))
   [:th.filter {:on-click #(toggle-el "listing-filters")}
    [:span.icon.is-small
     [:i.fas.fa-filter]]]])

(defn table-filter-row [params]
  [:tr#listing-filters.is-hidden
   [:th]
   [:th]
   [:th>div.field
    [:div.control
     [input-el 'username 'username 'text "input" "" "username" params]]]
   [:th>div.field
    [:div.control
     [input-el 'first-name 'first-name 'text "input" "" "first-name" params]]]
   [:th>div.field
    [:div.control
     [input-el 'last-name 'last-name 'text "input" "" "last-name" params]]]
   [:th>div.field
    [:div.control
     [input-el 'email 'email 'text "input" "" "email" params]]]
   [:th>div.field
    [:div.control
     [input-el 'designation 'designation 'text "input" "" "designation" params]]]
   [:th
    [:div.field
     [:div.control.is-expanded.has-icons-left
      [input-el "last-login-from" "last-login-from" "text"
       "input input-datepicker date-from" "From" "last-login-from" params]
      [:span.icon.is-small.is-left.open-date-from
       [:span.fas.fa-calendar]]]]
    [:div.field
     [:div.control.is-expanded.has-icons-left
      [input-el "last-login-to" "last-login-to" "text"
       "input input-datepicker date-to" "To" "last-login-to" params]
      [:span.icon.is-small.is-left.open-date-to
       [:span.fas.fa-calendar]]]]]
   [:th]
   [:th {:col-span "2"}
    [:div.buttons
     [:button.clear-filter-button.button.is-fullwidth
      {:type "button"
       :on-click #(clear params)} "Clear"]
     [:button.filter-button.button.is-fullwidth.is-primary
      {:type "button"
       :on-click #(get-users params)} "Search"]]]])

(defn table-ui [params]
  [:table.listing-table.table.is-fullwidth.is-striped.is-hoverable
   [:thead
    [table-head-row params]
    [table-filter-row params]]
   [:tbody.listing-content
    (for [{:keys [no id username first-name last-name email
                  designation last-login date-created enabled]} @users]
      ^{:key id}
      [:tr {:style {:border "none"}}
       [:td {:style {:border "none"}}
        [:label.checkbox
         [:input {:type "checkbox" :name "id" :value id}]]]
       [:td {:style {:border "none"}} no]
       [:td {:style {:border "none"}} username]
       [:td {:style {:border "none"}} first-name]
       [:td {:style {:border "none"}} last-name]
       [:td {:style {:border "none"}} email]
       [:td {:style {:border "none"}} designation]
       [:td {:style {:border "none"}} (util/format-date-time last-login)]
       [:td {:style {:border "none"}} (util/format-date-time date-created)]
       [:td {:col-span "2"
             :style {:border "none"}} enabled]])]])

(defn content []
  (let [params (rcore/atom {:offset 0 :limit 15})]
    (get-users params)
    ;; (let [fields (rcore/atom {})
    ;;       errors (rcore/atom nil)]
    ;;   )
    (fn []
      [:div
       [:div#notice]
       [quick-filter params]
       [:div.listing.table-container.is-sortable
        [:form.listing-filter-form {:auto-complete "off"
                                    :method "POST",
                                    :action "#"}
         [input-el 'limit 'limit 'hidden "" "" "offset" params]
         [input-el 'offset 'offset 'hidden "" "" "offset" params]
         [table-ui params]
         ]]


       ;; [:div.content>div.columns.is-multiline
       ;;  [:div.column
       ;;   [table-ui users]
       ;;   [pagination-ui current-pg last-pg]
       ;;   [action-ui fields errors]
       ;;   [modal-ui fields errors]
       ;;   ]]
       ])))

(rdom/render [page-el/topbar] (gdom/getElement "topbar"))
(rdom/render [content] (gdom/getElement "content"))

(loader/set-loaded! :user)
