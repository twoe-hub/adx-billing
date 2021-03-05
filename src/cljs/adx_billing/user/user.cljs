(ns adx-billing.user.user
  (:require [ajax.core :refer [GET POST]]
            [cljs.loader :as loader]
            [clojure.string :as string]
            [goog.dom :as gdom]
            [reagent.core :as rcore]
            [reagent.dom :as rdom]
            [adx-billing.common.listing :as ls]
            [adx-billing.common.util :as util]
            [adx-billing.common.page-el :as page-el]
            [adx-billing.msg.bundle :refer [msg]]
            [adx-billing.user.validate :refer [validate]]
            [adx-billing.common.util :refer [toggle-el hide-el]]))

(defonce url "/user/users")
(defonce cols ['id 'no 'username 'first-name 'last-name 'email 'designation 'last-login 'date-created 'enabled])
(defonce no-sort-cols ['no])
(defonce params (rcore/atom {:sort "username" :order 1 :offset 0 :limit ls/pg-size}))
(defonce users (rcore/atom {}))
(defonce counts (rcore/atom {}))

(defn handler [m]
  (do
    (reset! users (map
                  #(assoc %
                          :date-created (util/parse-date-time (:date-created %))
                          :last-login (util/parse-date-time (:last-login %)))
                  (:records m)))
    (reset! counts (:counts m))))

(defn show-modal [elem]
  (.add (.-classList elem) "is-active")
  (.focus elem))

(defn hide-modal [elem fields errors]
  (reset! fields {})
  (reset! errors nil)
  (.remove (.-classList elem) "is-active"))

(defn toggle-modal
  ([class fields errors]
   (let [elem (gdom/getElement "edit-modal")]
     (if (.contains (.-classList elem) "is-active")
       (hide-modal elem fields errors)
       (show-modal elem))))
  ([e class fields errors]
   (when (= (.-key e) "Escape")
     (toggle-modal class fields errors))))

(defn save-user [fields errors users]
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

(defn errors-component [errors id]
  (when-let [error (id @errors)]
    [:p.help.is-danger (string/join error)]))

(defn input-el
  "An input element which updates its value on change"
  [id name type class placeholder param-name]
  [:input {:id id
           :name name
           :type type
           :class class
           :placeholder placeholder
           :value ((keyword param-name) @params)
           :on-change #(swap! params assoc
                              (keyword param-name) (-> % .-target .-value))
           }])

(defn edit-form [fields errors]
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
      :on-key-up #(toggle-modal % modal-id fields errors)
      :style {:justify-content "flex-start"
              :padding-top "150px"}}
     [:div.modal-background]
     [:div.modal-card
      [:header.modal-card-head
       [:p.modal-card-title ""]
       [:button.delete
        {:aria-label "close"
         :on-click #(toggle-modal modal-id fields errors)}]]
      [:section.modal-card-body [(edit-form fields errors)]]
      [:footer.modal-card-foot.is-right.pt-50.pb-20.pr-20
       {:style {:justify-content "right"}}
       [:button.button {:on-click #(toggle-modal modal-id fields errors)}
        "Cancel"]
       [:button.button {:on-click #(save-user fields errors users)}
        "Save"]]]]))

(defn action-ui [fields errors]
  [:div.field.is-grouped.is-grouped-centered
   [:div.control
    [:button.button {:disabled true} "Delete"]]
   [:div.control
    [:button.button.pl-25.pr-25
     {:on-click #(toggle-modal "edit-modal" fields errors)} "Create"]]])

(defn quick-filter []
  [:div#quick-filter.tabs.is-flex
   [:ul
    [:li {:key "all"}
     [:a {:on-click #((swap! params dissoc :enabled)
                      (ls/get-records url params handler))}
      (str (msg (keyword (str "user.qf-label/" "all"))) ": " (@counts :all 0))]]
    [:li {:key "active"}
     [:a {:on-click #((swap! params assoc :enabled "true")
                      (ls/get-records url params handler))}
      (str (msg (keyword (str "user.qf-label/" "active"))) ": " (@counts :active 0))]]
    [:li {:key "inactive"}
     [:a {:on-click #((swap! params assoc :enabled "false")
                      (ls/get-records url params handler))}
      (str (msg (keyword (str "user.qf-label/" "inactive"))) ": " (@counts :inactive 0))]]]])

(defn table-filter-row []
  [:tr#listing-filter.is-hidden
   [:th]
   [:th]
   [:th>div.field
    [:div.control
     [input-el 'username 'username 'text "input" "" "username"]]]
   [:th>div.field
    [:div.control
     [input-el 'first-name 'first-name 'text "input" "" "first-name"]]]
   [:th>div.field
    [:div.control
     [input-el 'last-name 'last-name 'text "input" "" "last-name"]]]
   [:th>div.field
    [:div.control
     [input-el 'email 'email 'text "input" "" "email"]]]
   [:th>div.field
    [:div.control
     [input-el 'designation 'designation 'text "input" "" "designation"]]]
   [:th
    [:div.field
     [:div.control.is-expanded.has-icons-left
      [input-el "last-login-from" "last-login-from" "text"
       "input input-datepicker date-from" "From" "last-login-from"]
      [:span.icon.is-small.is-left.open-date-from
       [:span.fas.fa-calendar]]]]
    [:div.field
     [:div.control.is-expanded.has-icons-left
      [input-el "last-login-to" "last-login-to" "text"
       "input input-datepicker date-to" "To" "last-login-to"]
      [:span.icon.is-small.is-left.open-date-to
       [:span.fas.fa-calendar]]]]]
   [:th]
   [:th {:col-span "2"}
    [:div.buttons
     [:button.clear-filter-button.button.is-fullwidth
      {:type "button"
       :on-click #(ls/clear params)} "Clear"]
     [:button.filter-button.button.is-fullwidth.is-primary
      {:type "button"
       :on-click #(do
                    (swap! params assoc :offset 0)
                    (ls/get-records url params handler))} "Search"]]]])

(defn table-ui []
  [:table.listing-table.table.is-fullwidth.is-striped.is-hoverable
   [:thead
    [ls/table-head-row "user" cols no-sort-cols url params handler]
    (table-filter-row)]
   [:tbody.listing-content
    (for [{:keys [id no username first-name last-name email
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
  (let [fields (rcore/atom {})
        errors (rcore/atom nil)]
    (ls/get-records url params handler)
    (fn []
      [:div
       [:div#notice]
       (quick-filter)
       [:div.listing.table-container.is-sortable
        [:form.listing-filter-form {:auto-complete "off"
                                    :method "POST",
                                    :action "#"}
         [input-el 'limit 'limit 'hidden "" "" "offset"]
         [input-el 'offset 'offset 'hidden "" "" "offset"]
         (table-ui)]]
       [ls/pagination-ui url params handler]
       [action-ui fields errors]
       [modal-ui fields errors]])))

(rdom/render [page-el/topbar] (gdom/getElement "topbar"))
(rdom/render [content] (gdom/getElement "content"))

(loader/set-loaded! :user)
