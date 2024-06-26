create table if not exists users
(
    username    varchar(50)  NOT NULL unique primary key,
    password    varchar(200) NOT NULL,
    firstname   varchar(50)  NOT NULL,
    lastname    varchar(50)  NOT NULL,
    phonenumber varchar(50),
    dateOfBirth varchar(50)
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
    id         integer primary key autoincrement,
    username   varchar(50) NOT NULL,
    year       integer     NOT NULL,
    month      integer     NOT NULL,
    day        integer     NOT NULL,
    hour       integer     NOT NULL,
    barberId   integer     NOT NULL,
    hairLength VARCHAR(20) NOT NULL,
    foreign key (barberId) references barbers (id),
    unique (year, month, day, hour, barberId)
);
create table if not exists services
(
    id              varchar(50) NOT NULL unique primary key,
    name            varchar(50) NOT NULL,
    name_en         varchar(50) NOT NULL,
    price           real        NOT NULL,
    isBarberService boolean     NOT NULL
);
create table if not exists service_of_reservation
(
    id            integer primary key autoincrement,
    reservationid integer     NOT NULL,
    serviceid     varchar(50) NOT NULL,
    foreign key (serviceid) references services (id),
    foreign key (reservationid) references reservations (id)
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
INSERT INTO services (id, name, name_en, price, isBarberService)
values ('hairdressingCut', 'Leikkaus, pesu ja kuivaus', 'Hair washing, cutting and drying', 45, false);
INSERT INTO services (id, name, name_en, price, isBarberService)
values ('hairdressingNewStyle', 'Mallinmuutosleikkaus', 'Hair model change', 70, false);
INSERT INTO services (id, name, name_en, price, isBarberService)
values ('hairdressingCutForehead', 'Otsahiusten leikkaus', 'Forehead hair cutting', 15, false);
INSERT INTO services (id, name, name_en, price, isBarberService)
values ('hairdressingColor', 'Hiusten värjäys', 'Hair coloring', 100, false);
INSERT INTO services (id, name, name_en, price, isBarberService)
values ('hairdressingColorRootGrowth', 'Juurikasvun värjäys', 'Root growth coloring', 50, false);
INSERT INTO services (id, name, name_en, price, isBarberService)
values ('barberCut', 'Leikkaus, pesu ja kuivaus', 'Hair washing, cutting and drying', 25, true);
INSERT INTO services (id, name, name_en, price, isBarberService)
values ('barberNewStyle', 'Mallinmuutosleikkaus', 'Hair model change', 40, true);
INSERT INTO services (id, name, name_en, price, isBarberService)
values ('barberMachineCut', 'Parturi konetyö', 'Machine cut', 15, true);
INSERT INTO services (id, name, name_en, price, isBarberService)
values ('barberColor', 'Hiusten värjäys', 'Hair coloring', 75, true);
INSERT INTO services (id, name, name_en, price, isBarberService)
values ('barberColorRootGrowth', 'Juurikasvun värjäys', 'Root growth coloring', 35, true);
INSERT INTO services (id, name, name_en, price, isBarberService)
values ('barberBeard', 'Parran muotoilu', 'Beard trimming', 10, true);