-- :name save-message! :! :n
-- :doc creates a new message
INSERT INTO public.guestbook
(name, message, timestamp)
VALUES (:name, :message, :timestamp)

-- :name get-messages :? :*
-- :doc selects all messages
SELECT * FROM public.guestbook

-- :name get-modules :? :*
-- :doc selects all modules
SELECT m.* FROM public.module m
where m.access in (:v*:access) or m.access is null
order by m.ordinal

-- :name get-access :? :*
-- :doc selects all access for given user
SELECT a.name FROM public.user u
join public.user_role ur on ur.user_id = u.id
join public.role r on r.id = ur.role_id
join public.role_access ra on ra.role_id = r.id
join public.access a on a.id = ra.access_id
where u.id = :uid

-- :name auth! :? :1
-- :doc select user for authentication
SELECT u.*, a.name aff_name FROM public.user u
join public.affiliate a on a.id = u.aff_id
where u.username = :username

-- :name create-user! :! :n
-- :doc creates a new user
INSERT INTO public.user
(username, first_name, last_name, email)
VALUES (:username, :first-name, :last-name, :email)

-- :name get-users :? :*
-- :doc selects all users
SELECT u.id, u.username, u.first_name, u.last_name, u.email, u.designation, u.last_login,
       u.date_created, u.enabled
FROM public.user u OFFSET :offset LIMIT :limit

-- :name count-users :? :1
-- :doc count all users
SELECT COUNT(u.id)
FROM public.user u
