create table account (
	id               bigint        not null auto_increment,
	gbp_total_amount decimal(10,2) not null default 0,
	eur_total_amount decimal(10,2) not null default 0,
	usd_total_amount decimal(10,2) not null default 0,
	create_date      timestamp not null default now(),
	update_date      timestamp not null default now(),
	status           enum ('ACTIVE', 'INACTIVE') not null default 'ACTIVE',
	primary key (id)
);

create table user_account (
	id          bigint       not null auto_increment,
	account_id  bigint       not null,
	name        varchar(255) not null,
	create_date timestamp    not null default now(),
	update_date timestamp    not null default now(),
	status      enum ('ACTIVE', 'INACTIVE') not null default 'ACTIVE',
	primary key (id),
	constraint fk_account foreign key (account_id) references account (id)
);

create table deposit_historic (
	id          bigint     not null auto_increment,
	user_id     bigint     not null,
	currency    enum ('GBP', 'EUR', 'USD') not null,
	amount      decimal(10,2) not null,
	create_date timestamp  not null default now(),
	update_date timestamp  not null default now(),
	status      enum ('ACTIVE', 'INACTIVE') not null default 'ACTIVE',
	primary key (id),
	constraint fk_user_deposit foreign key (user_id) references user_account (id)
);

create table withdraw_historic (
	id          bigint    not null auto_increment,
	user_id     bigint    not null,
	currency    enum ('GBP', 'EUR', 'USD') not null,
	amount      decimal(10,2) not null,
	create_date timestamp not null default now(),
	update_date timestamp not null default now(),
	status      enum ('ACTIVE', 'INACTIVE') not null default 'ACTIVE',
	primary key (id),
	constraint fk_user_withdraw foreign key (user_id) references user_account (id)
);

create table audit_log (
	id               bigint           not null auto_increment,
	user_id          bigint           not null,
	transaction_type enum ('DEPOSIT', 'WITHDRAW') not null,
	currency         enum ('GBP', 'EUR', 'USD') not null,
	amount           decimal(10,2)    not null,
	create_date      timestamp        not null default now(),
	update_date      timestamp        not null default now(),
	primary key (id),
	status           enum ('ACTIVE', 'INACTIVE') not null default 'INACTIVE'
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