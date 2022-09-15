create table IF NOT EXISTS date_times
(
    id              bigint PRIMARY KEY AUTO_INCREMENT,
    local_date      date,
    local_date_time timestamp,
    local_time      timestamp,
    zoned_date_time TIMESTAMP WITH TIME ZONE
);

insert into date_times (id, local_date, local_date_time, local_time, zoned_date_time)
values (1, '2022-09-15', null, null, null);

insert into date_times (id, local_date, local_date_time, local_time, zoned_date_time)
values (2, null, null, TIME '11:45:17.181047', null);

insert into date_times (id, local_date, local_date_time, local_time, zoned_date_time)
values (3, null, TIMESTAMP '2022-09-15 11:46:37.809162', null, null);

insert into date_times (id, local_date, local_date_time, local_time, zoned_date_time)
values (4, null, null, null, TIMESTAMP WITH TIME ZONE '2022-09-15 11:45:17.061287+02');