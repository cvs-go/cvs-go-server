SET FOREIGN_KEY_CHECKS = 0;
drop table if exists category cascade;
drop table if exists convenience_store cascade;
drop table if exists event cascade;
drop table if exists manufacturer cascade;
drop table if exists product cascade;
drop table if exists product_bookmark cascade;
drop table if exists product_like cascade;
drop table if exists promotion cascade;
drop table if exists refresh_token cascade;
drop table if exists review cascade;
drop table if exists review_image cascade;
drop table if exists review_like cascade;
drop table if exists sell_at cascade;
drop table if exists tag cascade;
drop table if exists user cascade;
drop table if exists user_follow cascade;
drop table if exists user_tag cascade;
SET FOREIGN_KEY_CHECKS = 1;

create table category (
                          id bigint not null auto_increment,
                          name varchar(50) not null unique,
                          created_at datetime,
                          modified_at datetime,
                          primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table convenience_store (
                                   id bigint not null auto_increment,
                                   name varchar(50) not null unique,
                                   created_at datetime,
                                   modified_at datetime,
                                   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table event (
                       id bigint not null auto_increment,
                       event_type varchar(10) not null,
                       product_id bigint,
                       convenience_store_id bigint,
                       discount_amount integer,
                       gift_product_id bigint,
                       created_at datetime,
                       modified_at datetime,
                       primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table manufacturer (
                              id bigint not null auto_increment,
                              name varchar(50) not null unique,
                              created_at datetime,
                              modified_at datetime,
                              primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table product (
                         id bigint not null auto_increment,
                         created_at datetime,
                         modified_at datetime,
                         image_url varchar(255),
                         like_count bigint default 0 not null,
                         name varchar(50) not null,
                         price integer,
                         version bigint,
                         category_id bigint,
                         manufacturer_id bigint,
                         primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table product_bookmark (
                                  id bigint not null auto_increment,
                                  user_id bigint not null,
                                  product_id bigint not null,
                                  created_at datetime,
                                  modified_at datetime,
                                  primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table product_like (
                             id bigint not null auto_increment,
                             user_id bigint not null,
                             product_id bigint not null,
                             created_at datetime,
                             modified_at datetime,
                             primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table promotion (
                           id bigint not null auto_increment,
                           created_at datetime,
                           modified_at datetime,
                           image_url varchar(255),
                           primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table refresh_token (
                               id bigint not null auto_increment,
                               user_id bigint,
                               token varchar(255) not null,
                               created_at datetime,
                               modified_at datetime,
                               primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table review (
                        id bigint not null auto_increment,
                        created_at datetime,
                        modified_at datetime,
                        content TEXT not null,
                        rating integer,
                        product_id bigint,
                        user_id bigint,
                        primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table review_image (
                              id bigint not null auto_increment,
                              image_url varchar(255) not null,
                              review_id bigint not null,
                              created_at datetime,
                              modified_at datetime,
                              primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table review_like (
                             id bigint not null auto_increment,
                             user_id bigint not null,
                             review_id bigint not null,
                             created_at datetime,
                             modified_at datetime,
                             primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table sell_at (
                         id bigint not null auto_increment,
                         product_id bigint not null,
                         convenience_store_id bigint not null,
                         created_at datetime,
                         modified_at datetime,
                         primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table tag (
                     id bigint not null auto_increment,
                     tag_group integer not null,
                     name varchar(10) not null unique,
                     created_at datetime,
                     modified_at datetime,
                     primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table user (
                      id bigint not null auto_increment,
                      nickname varchar(16) not null unique,
                      password varchar(40) not null,
                      role varchar(20) not null,
                      user_id varchar(50) not null unique,
                      created_at datetime,
                      modified_at datetime,
                      primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table user_follow (
                             id bigint not null auto_increment,
                             following_id bigint not null,
                             follower_id bigint not null,
                             created_at datetime,
                             modified_at datetime,
                             primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table user_tag (
                          id bigint not null auto_increment,
                          tag_id bigint,
                          user_id bigint,
                          created_at datetime,
                          modified_at datetime,
                          primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;