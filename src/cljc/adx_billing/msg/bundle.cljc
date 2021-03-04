(ns adx-billing.msg.bundle
  (:require [taoensso.tempura :as tempura :refer [tr]]))

(def bundle
  {
   :en-GB {:missing "**Missing in en-GB**" ; Fallback for missing resources
           :menu {:text {:dashboard "Dashboard"
                         :org-profile "Company Profile"
                         :affs "Affiliates"
                         :docs "Documents"
                         :admin "Admin"
                         :audit-log "Audit Log"
                         :about "About"
                         :doc-quote "Quotation"
                         :user "Users"
                         :role "Roles"
                         :pwd-policy "Password Policy"}
                  :icon {:dashboard "fa-tachometer-alt"
                         :org-profile "fa-building"
                         :affs "fa-handshake"
                         :docs "fa-file-alt"
                         :admin "fa-user-shield"
                         :audit-log "fa-list-alt"
                         :about "fa-info-circle"}}
           :aff {:qf-label {:active "Active"
                            :inactive "Inactive"
                            :all "All"}
                  :cols {:no "No."
                         :code "Code"
                         :name "Name"
                         :reg-no "Reg. No."
                         :tax-no "Tax No."
                         :entity-type "Entity Type"
                         :industry-id "Industry"
                         :date-est "Established"
                         :website "Website"}}
           :qutn {:qf-label {:draft "Draft"
                             :sent "Sent"
                             :approved "Approved"
                             :completed "Completed"
                             :all "All"}
                 :cols {:no "No."
                        :quote-no "Quote No."
                        :issued-to "Issued To"
                        :value "Value"
                        :issued-by "Issued By"
                        :date-issued "Date Issued"
                        :cat "Category"
                        :sub-cat "Sub-category"}}
           :user {:qf-label {:active "Active"
                             :inactive "Inactive"
                             :all "All"}
                  :cols {:no "No."
                         :username "Username"
                         :first-name "First Name"
                         :last-name "Last Name"
                         :email "Email"
                         :designation "Designation"
                         :last-login "Last Login"
                         :date-created "Created on"
                         :enabled "Status"}}
           :validation {:invalid-token "Invalid anti-forgery token"
                        :required "Required"
                        :invalid-email "Invalid email address"
                        :char-limit-exceeded "Must not contain more than %1 characters"}
           :error {:very-bad {:title "Something very bad has happened!"
                              :msg "We've dispatched a team of highly trained gnomes to take care of the problem."}}}

   :en {:missing "**Missing**"
        :copy-all :en-GB}})

(defn msg
  "Get a localized resource.

  @param resource Resource keyword.
  @param params   Optional positional parameters.

  @return translation of `resource` in active user language or a placeholder."
  [resource & params]
  (let [lang :en-GB] ; Retrieve user language from database or other source
    (tr {:dict bundle} [lang :en] [resource] (vec params))))
