create table if not exists guestbook
(id serial primary key,
name text,
message text,
timestamp timestamp);
--;;
create table if not exists public.user
(id uuid primary key default uuid_generate_v4(),
username text not null unique,
first_name text not null,
last_name text not null,
email text not null unique);
--;;
insert into public.user (username, first_name, last_name, email) values ('adeel.gnome1@gmail.com', 'Adeel', 'Ansari', 'adeel.gnome1@gmail.com');
--;;
insert into public.user (username, first_name, last_name, email) values ('adeel.gnome2@gmail.com', 'Adeel', 'Ansari', 'adeel.gnome2@gmail.com');
--;;
insert into public.user (username, first_name, last_name, email) values ('adeel.gnome3@gmail.com', 'Adeel', 'Ansari', 'adeel.gnome3@gmail.com');
--;;
insert into public.user (username, first_name, last_name, email) values ('adeel.gnome4@gmail.com', 'Adeel', 'Ansari', 'adeel.gnome4@gmail.com');
--;;
insert into public.user (username, first_name, last_name, email) values ('adeel.gnome5@gmail.com', 'Adeel', 'Ansari', 'adeel.gnome5@gmail.com');
--;;
insert into public.user (username, first_name, last_name, email) values ('adeel.gnome6@gmail.com', 'Adeel', 'Ansari', 'adeel.gnome6@gmail.com');
--;;
insert into public.user (username, first_name, last_name, email) values ('adeel.gnome7@gmail.com', 'Adeel', 'Ansari', 'adeel.gnome7@gmail.com');
--;;
insert into public.user (username, first_name, last_name, email) values ('adeel.gnome8@gmail.com', 'Adeel', 'Ansari', 'adeel.gnome8@gmail.com');
--;;
insert into public.user (username, first_name, last_name, email) values ('adeel.gnome9@gmail.com', 'Adeel', 'Ansari', 'adeel.gnome9@gmail.com');
--;;
insert into public.user (username, first_name, last_name, email) values ('adeel.gnome10@gmail.com', 'Adeel', 'Ansari', 'adeel.gnome10@gmail.com');
--;;
insert into public.user (username, first_name, last_name, email) values ('adeel.gnome11@gmail.com', 'Adeel', 'Ansari', 'adeel.gnome11@gmail.com');
--;;
insert into public.user (username, first_name, last_name, email) values ('adeel.gnome12@gmail.com', 'Adeel', 'Ansari', 'adeel.gnome12@gmail.com');
--;;
insert into public.user (username, first_name, last_name, email) values ('adeel.gnome13@gmail.com', 'Adeel', 'Ansari', 'adeel.gnome13@gmail.com');
--;;
insert into public.user (username, first_name, last_name, email) values ('adeel.gnome14@gmail.com', 'Adeel', 'Ansari', 'adeel.gnome14@gmail.com');
--;;
insert into public.user (username, first_name, last_name, email) values ('adeel.gnome15@gmail.com', 'Adeel', 'Ansari', 'adeel.gnome15@gmail.com');
--;;
insert into public.user (username, first_name, last_name, email) values ('adeel.gnome16@gmail.com', 'Adeel', 'Ansari', 'adeel.gnome16@gmail.com');
--;;
insert into public.user (username, first_name, last_name, email) values ('adeel.gnome17@gmail.com', 'Adeel', 'Ansari', 'adeel.gnome17@gmail.com');
--;;
