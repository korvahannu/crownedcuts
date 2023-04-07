create table if not exists users
(
    username varchar(50)  NOT NULL unique primary key,
    password varchar(200) NOT NULL
);
create table if not exists roles
(
    username varchar(50) NOT NULL,
    userRole varchar(50) NOT NULL,
    primary key (username, userRole),
    foreign key (username) references users (username)
);
create table if not exists barbers
(
    id   integer primary key autoincrement,
    name varchar(50)
);
create table if not exists reservations
(
    username varchar(50) NOT NULL,
    year     integer     NOT NULL,
    month    integer     NOT NULL,
    day      integer     NOT NULL,
    hour     integer     NOT NULL,
    barberId integer     NOT NULL,
    primary key (barberId, year, month, day, hour)
);
insert into barbers (name)
values ('Milla Pyrrö');
insert into barbers (name)
values ('Hannu Korvala');
insert into barbers (name)
values ('Antti Juustila');
insert into barbers (name)
values ('Mikko Rajanen');
insert into barbers (name)
values ('Jouni Lappalainen');
