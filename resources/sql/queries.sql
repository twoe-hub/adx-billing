-- :name get-modules :? :*
-- :doc selects all modules
SELECT m.* FROM public.module m
WHERE m.access IN (:v*:access) OR m.access IS NULL
ORDER BY m.ordinal

-- :name get-access :? :*
-- :doc selects all access for given user
SELECT a.name FROM public.user u
JOIN public.user_role ur ON ur.user_id = u.id
JOIN public.role r ON r.id = ur.role_id
JOIN public.role_access ra ON ra.role_id = r.id
JOIN public.access a ON a.id = ra.access_id
WHERE u.id = :uid

-- :name auth! :? :1
-- :doc select user for authentication
SELECT u.*, a.name aff_name
FROM public.user u
JOIN public.affiliate a ON a.id = u.aff_id
WHERE u.username = :username
AND u.enabled = 't'

-- :name create-user! :! :n
-- :doc creates a new user
INSERT INTO public.user
(username, first_name, last_name, email)
VALUES (:username, :first-name, :last-name, :email)

-- :name get-users :? :*
-- :require [adx-billing.db.util :refer [str-regex]]
-- :doc selects all users
SELECT row_number() over (ORDER BY :i:sort
/*~ (when (= (:order params) "desc") */
DESC
/*~ ) ~*/
) as no, u.id, u.username, u.first_name, u.last_name, u.email, u.designation, u.last_login, u.date_created, u.enabled
FROM public.user u
WHERE 1 = 1
/*~ (when (not (clojure.string/blank? (:enabled params))) */
AND u.enabled = :enabled::boolean
/*~ ) ~*/
:snip:cond
OFFSET :offset LIMIT :limit

-- :name count-users :? :*
-- :require [adx-billing.db.util :refer [str-regex]]
-- :doc count all users
SELECT u.enabled status, count(u.*) count
FROM public.user u
WHERE 1 = 1
:snip:cond
GROUP BY u.enabled
ORDER BY u.enabled desc

-- :snip cond-users
/*~ (when (not (clojure.string/blank? (:username params))) */
AND u.username ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:username params))) (str-regex (:username params)))
/*~ (when (not (clojure.string/blank? (:first-name params))) */
AND u.first_name ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:first-name params))) (str-regex (:first-name params)))
/*~ (when (not (clojure.string/blank? (:last-name params))) */
AND u.last_name ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:last-name params))) (str-regex (:last-name params)))
/*~ (when (not (clojure.string/blank? (:email params))) */
AND u.email ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:email params))) (str-regex (:email params)))
/*~ (when (not (clojure.string/blank? (:designation params))) */
AND u.designation ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:designation params))) (str-regex (:designation params)))

-- :name get-affs :? :*
-- :doc selects all affiliates
SELECT row_number() over (ORDER BY :i:sort
/*~ (when (= (:order params) "desc") */
DESC
/*~ ) ~*/
) as no, a.id, a.code, a.name, a.reg_no, a.tax_no, a.entity_type, i.name AS industry, a.date_est, a.website
FROM public.affiliate a
JOIN public.industry i on i.id = a.industry_id
WHERE 1 = 1
/*~ (when (not (clojure.string/blank? (:status params))) */
AND a.status = :status
/*~ ) ~*/
:snip:cond
OFFSET :offset LIMIT :limit

-- :name count-affs :? :*
-- :doc count all affiliates
SELECT a.status status, count(a.id) count
FROM public.affiliate a
JOIN public.industry i on i.id = a.industry_id
WHERE 1 = 1
:snip:cond
GROUP BY a.status
ORDER BY a.status desc

-- :snip cond-affs
/*~ (when (not (clojure.string/blank? (:code params))) */
AND a.code ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:code params))) (str-regex (:code params)))
/*~ (when (not (clojure.string/blank? (:name params))) */
AND a.name ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:name params))) (str-regex (:name params)))
/*~ (when (not (clojure.string/blank? (:reg-no params))) */
AND a.reg_no ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:reg-no params))) (str-regex (:reg-no params)))
/*~ (when (not (clojure.string/blank? (:tax-no params))) */
AND a.tax_no ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:tax-no params))) (str-regex (:tax-no params)))
/*~ (when (not (clojure.string/blank? (:entity-type params))) */
AND a.entity_type ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:entity-type params))) (str-regex (:entity-type params)))
/*~ (when (not (clojure.string/blank? (:industry params))) */
AND i.name ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:industry params))) (str-regex (:industry params)))

-- :name get-qutns :? :*
-- :require [adx-billing.db.util :refer [str-regex]]
-- :doc selects all quotations
SELECT row_number() over (ORDER BY :i:sort
/*~ (when (= (:order params) "desc") */
DESC
/*~ ) ~*/
) AS no, q.id, q.quote_no, p_to.aff_name AS issued_to, q.value, p_by.aff_name AS issued_by, q.date_issued, c.name AS cat, sc.name AS sub_cat
FROM public.quotation q
JOIN public.category c on c.id = q.cat_id
JOIN public.sub_category sc on sc.id = q.sub_cat_id
JOIN public.party p_to on p_to.id = q.issued_to
JOIN public.party p_by on p_by.id = q.issued_by
WHERE 1 = 1
/*~ (when (not (clojure.string/blank? (:status params))) */
AND q.status = :status
/*~ ) ~*/
:snip:cond
OFFSET :offset LIMIT :limit

-- :name count-qutns :? :*
-- :require [adx-billing.db.util :refer [str-regex]]
-- :doc count all quotations
SELECT q.status, count(q.id) count
FROM public.quotation q
JOIN public.category c on c.id = q.cat_id
JOIN public.sub_category sc on sc.id = q.sub_cat_id
JOIN public.party p_to on p_to.id = q.issued_to
JOIN public.party p_by on p_by.id = q.issued_by
WHERE 1 = 1
:snip:cond
GROUP BY q.status

-- :snip cond-qutns
/*~ (when (not (clojure.string/blank? (:quote-no params))) */
AND q.quote_no ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:quote-no params))) (str-regex (:quote-no params)))
/*~ (when (not (clojure.string/blank? (:issued-to params))) */
AND p_to.aff_name ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:issued-to params))) (str-regex (:issued-to params)))
/*~ (when (not (clojure.string/blank? (:issued-by params))) */
AND p_by.aff_name ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:issued-by params))) (str-regex (:issued-by params)))
/*~ (when (not (clojure.string/blank? (:cat params))) */
AND c.name ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:cat params))) (str-regex (:cat params)))
/*~ (when (not (clojure.string/blank? (:sub-cat params))) */
AND sc.name ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:sub-cat params))) (str-regex (:sub-cat params)))

-- :name get-wos :? :*
-- :require [adx-billing.db.util :refer [str-regex]]
-- :doc selects all work-orders
SELECT row_number() over (ORDER BY :i:sort
/*~ (when (= (:order params) "desc") */
DESC
/*~ ) ~*/
) AS no, i.id, i.wo_no, q.quote_no, p_to.aff_name AS issued_to, i.value, p_by.aff_name AS issued_by, i.date_issued, c.name AS cat, sc.name AS sub_cat
FROM public.work_order i
JOIN public.quote_wo_ref qw on qw.wo_id = wo.id
JOIN public.quotation q on q.id = qw.quote_id
JOIN public.category c on c.id = i.cat_id
JOIN public.sub_category sc on sc.id = i.sub_cat_id
JOIN public.party p_to on p_to.id = i.issued_to
JOIN public.party p_by on p_by.id = i.issued_by
WHERE 1 = 1
/*~ (when (not (clojure.string/blank? (:status params))) */
AND i.status = :status
/*~ ) ~*/
:snip:cond
OFFSET :offset LIMIT :limit

-- :name count-wos :? :*
-- :require [adx-billing.db.util :refer [str-regex]]
-- :doc count all work-orders
SELECT wo.status, count(wo.id) count
FROM public.work_order wo
JOIN public.quote_wo_ref qw on qw.wo_id = wo.id
JOIN public.quotation q on q.id = qw.quote_id
JOIN public.category c on c.id = wo.cat_id
JOIN public.sub_category sc on sc.id = wo.sub_cat_id
JOIN public.party p_to on p_to.id = wo.issued_to
JOIN public.party p_by on p_by.id = wo.issued_by
WHERE 1 = 1
:snip:cond
GROUP BY wo.status

-- :snip cond-wos
/*~ (when (not (clojure.string/blank? (:wo-no params))) */
AND wo.wo_no ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:wo-no params))) (str-regex (:wo-no params)))
/*~ (when (not (clojure.string/blank? (:quote-no params))) */
AND q.quote_no ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:quote-no params))) (str-regex (:quote-no params)))
/*~ (when (not (clojure.string/blank? (:issued-to params))) */
AND p_to.aff_name ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:issued-to params))) (str-regex (:issued-to params)))
/*~ (when (not (clojure.string/blank? (:issued-by params))) */
AND p_by.aff_name ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:issued-by params))) (str-regex (:issued-by params)))
/*~ (when (not (clojure.string/blank? (:cat params))) */
AND c.name ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:cat params))) (str-regex (:cat params)))
/*~ (when (not (clojure.string/blank? (:sub-cat params))) */
AND sc.name ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:sub-cat params))) (str-regex (:sub-cat params)))

-- :name get-invs :? :*
-- :require [adx-billing.db.util :refer [str-regex]]
-- :doc selects all invoices
SELECT row_number() over (ORDER BY :i:sort
/*~ (when (= (:order params) "desc") */
DESC
/*~ ) ~*/
) AS no, i.id, i.inv_no, wo.wo_no, q.quote_no, p_to.aff_name AS issued_to, i.value, p_by.aff_name AS issued_by, i.date_issued, c.name AS cat, sc.name AS sub_cat
FROM public.invoice i
JOIN public.quote_inv_ref qi on qi.inv_id = i.id
JOIN public.quotation q on q.id = qi.quote_id
JOIN public.wo_inv_ref wi on wi.inv_id = i.id
JOIN public.work_order wo on wo.id = wi.wo_id
JOIN public.category c on c.id = i.cat_id
JOIN public.sub_category sc on sc.id = i.sub_cat_id
JOIN public.party p_to on p_to.id = i.issued_to
JOIN public.party p_by on p_by.id = i.issued_by
WHERE 1 = 1
/*~ (when (not (clojure.string/blank? (:status params))) */
AND i.status = :status
/*~ ) ~*/
:snip:cond
OFFSET :offset LIMIT :limit

-- :name count-invs :? :*
-- :require [adx-billing.db.util :refer [str-regex]]
-- :doc count all invoices
SELECT i.status, count(i.id) count
FROM public.invoice i
JOIN public.quote_inv_ref qi on qi.inv_id = i.id
JOIN public.quotation q on q.id = qi.quote_id
JOIN public.wo_inv_ref wi on wi.inv_id = i.id
JOIN public.work_order wo on wo.id = wi.wo_id
JOIN public.category c on c.id = i.cat_id
JOIN public.sub_category sc on sc.id = i.sub_cat_id
JOIN public.party p_to on p_to.id = i.issued_to
JOIN public.party p_by on p_by.id = i.issued_by
WHERE 1 = 1
:snip:cond
GROUP BY i.status

-- :snip cond-invs
/*~ (when (not (clojure.string/blank? (:inv-no params))) */
AND i.inv_no ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:inv-no params))) (str-regex (:inv-no params)))
/*~ (when (not (clojure.string/blank? (:quote-no params))) */
AND q.quote_no ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:quote-no params))) (str-regex (:quote-no params)))
/*~ (when (not (clojure.string/blank? (:wo-no params))) */
AND wo.wo_no ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:wo-no params))) (str-regex (:wo-no params)))
/*~ (when (not (clojure.string/blank? (:issued-to params))) */
AND p_to.aff_name ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:issued-to params))) (str-regex (:issued-to params)))
/*~ (when (not (clojure.string/blank? (:issued-by params))) */
AND p_by.aff_name ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:issued-by params))) (str-regex (:issued-by params)))
/*~ (when (not (clojure.string/blank? (:cat params))) */
AND c.name ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:cat params))) (str-regex (:cat params)))
/*~ (when (not (clojure.string/blank? (:sub-cat params))) */
AND sc.name ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:sub-cat params))) (str-regex (:sub-cat params)))
