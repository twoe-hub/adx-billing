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
SELECT u.enabled status, count(*) count
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
) as no, a.id, a.code, a.name, a.reg_no, a.tax_no, a.entity_type, a.industry_id, a.date_est, a.website
FROM public.affiliate a
WHERE 1 = 1
OFFSET :offset LIMIT :limit

-- :name count-affs :? :*
-- :doc count all affiliates
SELECT a.status status, count(*) count
FROM public.affiliate a
WHERE 1 = 1
GROUP BY a.status
ORDER BY a.status desc

-- :name get-qutns :? :*
-- :require [adx-billing.db.util :refer [str-regex]]
-- :doc selects all quotations
SELECT row_number() over (ORDER BY :i:sort
/*~ (when (= (:order params) "desc") */
DESC
/*~ ) ~*/
) as no, q.id, q.username, q.first_name, q.last_name, q.email, q.designation, q.last_login, q.date_created, q.enabled
FROM public.quotation q
WHERE 1 = 1
/*~ (when (not (clojure.string/blank? (:enabled params))) */
AND q.enabled = :enabled::boolean
/*~ ) ~*/
:snip:cond
OFFSET :offset LIMIT :limit

-- :name count-qutns :? :*
-- :require [adx-billing.db.util :refer [str-regex]]
-- :doc count all quotations
SELECT q.enabled status, count(*) count
FROM public.quotation q
WHERE 1 = 1
:snip:cond
GROUP BY q.enabled
ORDER BY q.enabled desc

-- :snip cond-qutns
/*~ (when (not (clojure.string/blank? (:username params))) */
AND q.username ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:username params))) (str-regex (:username params)))
/*~ (when (not (clojure.string/blank? (:first-name params))) */
AND q.first_name ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:first-name params))) (str-regex (:first-name params)))
/*~ (when (not (clojure.string/blank? (:last-name params))) */
AND q.last_name ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:last-name params))) (str-regex (:last-name params)))
/*~ (when (not (clojure.string/blank? (:email params))) */
AND q.email ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:email params))) (str-regex (:email params)))
/*~ (when (not (clojure.string/blank? (:designation params))) */
AND q.designation ~*
/*~ ) ~*/
--~ (when (not (clojure.string/blank? (:designation params))) (str-regex (:designation params)))
