(ns adx-billing.msg.bundle
  (:require [taoensso.tempura :as tempura :refer [tr]]))

(def bundle
  {
   :en-GB {:missing "**Missing in en-GB**" ; Fallback for missing resources
           :menu {:dashboard "Dashboard"
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
           :validation {:required "Required"
                        :invalid-email "Invalid email address"
                        :char-limit-exceeded "Must not contain more than %1 characters"}}

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
