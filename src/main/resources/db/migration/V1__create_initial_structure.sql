create sequence account_id_seq;
create table account
(
	id               int primary key         default nextval('account_id_seq'),
	gbp_total_amount decimal(10, 2) not null default 0,
	eur_total_amount decimal(10, 2) not null default 0,
	usd_total_amount decimal(10, 2) not null default 0,
	create_date      timestamp      not null default now(),
	update_date      timestamp      not null default now(),
	status           string         not null default 'ACTIVE'
);

create sequence user_account_id_seq;
create table user_account
(
	id          int primary key    default nextval('user_account_id_seq'),
	account_id  int       not null,
	name        string    not null,
	create_date timestamp not null default now(),
	update_date timestamp not null default now(),
	status      string    not null default 'ACTIVE',
	constraint fk_account foreign key (account_id) references account (id)
);

create sequence deposit_historic_id_seq;
create table deposit_historic
(
	id          int primary key         default nextval('deposit_historic_id_seq'),
	user_id     int            not null,
	currency    string         not null,
	amount      decimal(10, 2) not null,
	create_date timestamp      not null default now(),
	update_date timestamp      not null default now(),
	status      string         not null default 'ACTIVE',
	constraint fk_user_deposit foreign key (user_id) references user_account (id)
);

create sequence withdraw_historic_id_seq;
create table withdraw_historic
(
	id          int primary key         default nextval('withdraw_historic_id_seq'),
	user_id     int            not null,
	currency    string         not null,
	amount      decimal(10, 2) not null,
	create_date timestamp      not null default now(),
	update_date timestamp      not null default now(),
	status      string         not null default 'ACTIVE',
	constraint fk_user_withdraw foreign key (user_id) references user_account (id)
);

INSERT INTO account (id, gbp_total_amount, eur_total_amount, usd_total_amount, create_date, update_date, status)
VALUES (1, 0, 0, 0, now(), now(), 'ACTIVE');
INSERT INTO account (id, gbp_total_amount, eur_total_amount, usd_total_amount, create_date, update_date, status)
VALUES (2, 0, 0, 0, now(), now(), 'ACTIVE');
INSERT INTO account (id, gbp_total_amount, eur_total_amount, usd_total_amount, create_date, update_date, status)
VALUES (3, 0, 0, 0, now(), now(), 'ACTIVE');

INSERT INTO user_account (id, account_id, name, create_date, update_date, status)
VALUES (1, 1, 'user1', now(), now(), 'ACTIVE');
INSERT INTO user_account (id, account_id, name, create_date, update_date, status)
VALUES (2, 2, 'user2', now(), now(), 'ACTIVE');
INSERT INTO user_account (id, account_id, name, create_date, update_date, status)
VALUES (3, 3, 'user3', now(), now(), 'ACTIVE');