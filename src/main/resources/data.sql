create table if not exists users (username varchar(50) NOT NULL unique primary key, password varchar(200) NOT NULL);
create table if not exists roles (username varchar(50) NOT NULL, userRole varchar(50) NOT NULL, primary key(username, userRole), foreign key(username) references users(username));