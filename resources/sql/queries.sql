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
order by m.ordinal

-- :name get-roles :? :*
-- :doc selects all access
SELECT a.name FROM public.access a

-- :name auth! :? :*
-- :doc select user for authentication
SELECT u.password FROM public.user u
where u.username=:username

-- :name create-user! :! :n
-- :doc creates a new user
INSERT INTO public.user
(username, first_name, last_name, email)
VALUES (:username, :first-name, :last-name, :email)

-- :name get-users :? :*
-- :doc selects all users
SELECT u.id, u.username, u.first_name, u.last_name, u.email
FROM public.user u OFFSET :offset LIMIT :limit

-- :name count-users :? :n
-- :doc count all users
SELECT COUNT(u.id)
FROM public.user u
