-- category
insert into category (name, created_at, modified_at) values ('간편식사', now(), now()), ('즉석요리', now(), now()), ('과자&빵', now(), now()), ('아이스크림', now(), now()), ('신선식품', now(), now()), ('유제품', now(), now()), ('음료', now(), now()), ('기타', now(), now());

-- convenience_store
insert into convenience_store (name, created_at, modified_at) values ('CU', now(), now()), ('GS25', now(), now()), ('세븐일레븐', now(), now()), ('Emart24', now(), now()), ('미니스톱', now(), now());

-- manufacturer
insert into manufacturer (name, created_at, modified_at) values ('오뚜기', now(), now()), ('크라운', now(), now()), ('매일', now(), now()), ('해태', now(), now()), ('농심', now(), now()), ('칠성', now(), now()), ('롯데', now(), now()), ('풀무원', now(), now());

-- product
insert into product (name, price, version, category_id, manufacturer_id, created_at, modified_at) values ('진라면순한맛', 950, 0, 2, 1, now(), now());
insert into product (name, price, version, category_id, manufacturer_id, created_at, modified_at) values ('마이쮸포도', 800, 0, 3, 2, now(), now());
insert into product (name, price, version, category_id, manufacturer_id, created_at, modified_at) values ('마이쮸딸기', 800, 0, 3, 2, now(), now());
insert into product (name, price, version, category_id, manufacturer_id, created_at, modified_at) values ('킨터초콜렛맥시', 900, 0, 3, 3, now(), now());
insert into product (name, price, version, category_id, manufacturer_id, created_at, modified_at) values ('칼카야버터', 800, 0, 6, 4, now(), now());
insert into product (name, price, version, category_id, manufacturer_id, created_at, modified_at) values ('백산수500ml', 950, 0, 7, 5, now(), now());
insert into product (name, price, version, category_id, manufacturer_id, created_at, modified_at) values ('아이시스500ml', 950, 0, 7, 6, now(), now());
insert into product (name, price, version, category_id, manufacturer_id, created_at, modified_at) values ('왓따 청포도', 500, 0, 8, 7, now(), now());
insert into product (name, price, version, category_id, manufacturer_id, created_at, modified_at) values ('왓따 복숭아', 500, 0, 8, 7, now(), now());
insert into product (name, price, version, category_id, manufacturer_id, created_at, modified_at) values ('풀무원샘물', 950, 0, 7, 8, now(), now());

-- sell_at
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (1, 1, now(), now()), (1, 2, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (2, 1, now(), now()), (2, 2, now(), now()), (2, 3, now(), now()), (2, 4, now(), now()), (2, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (3, 1, now(), now()), (3, 2, now(), now()), (3, 3, now(), now()), (3, 4, now(), now()), (3, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (4, 3, now(), now()), (4, 4, now(), now()), (4, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (5, 2, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (6, 1, now(), now()), (6, 2, now(), now()), (6, 3, now(), now()), (6, 4, now(), now()), (6, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (7, 1, now(), now()), (7, 2, now(), now()), (7, 3, now(), now()), (7, 4, now(), now()), (7, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (8, 1, now(), now()), (8, 2, now(), now()), (8, 3, now(), now()), (8, 4, now(), now()), (8, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (9, 1, now(), now()), (9, 2, now(), now()), (9, 3, now(), now()), (9, 4, now(), now()), (9, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (10, 1, now(), now()), (10, 2, now(), now()), (10, 3, now(), now()), (10, 4, now(), now()), (10, 5, now(), now());

-- event
insert into event (event_type, created_at, modified_at, discount_amount, convenience_store_id, product_id, gift_product_id) values ('GIFT', now(), now(), null, 2, 2, 3);
insert into event (event_type, created_at, modified_at, discount_amount, convenience_store_id, product_id, gift_product_id) values ('GIFT', now(), now(), null, 2, 3, 2);
insert into event (event_type, created_at, modified_at, discount_amount, convenience_store_id, product_id, gift_product_id) values ('BOGO', now(), now(), null, 3, 4, null);
insert into event (event_type, created_at, modified_at, discount_amount, convenience_store_id, product_id, gift_product_id) values ('BTGO', now(), now(), null, 4, 8, null);
insert into event (event_type, created_at, modified_at, discount_amount, convenience_store_id, product_id, gift_product_id) values ('BTGO', now(), now(), null, 4, 9, null);
insert into event (event_type, created_at, modified_at, discount_amount, convenience_store_id, product_id, gift_product_id) values ('BOGO', now(), now(), null, 1, 6, null);
insert into event (event_type, created_at, modified_at, discount_amount, convenience_store_id, product_id, gift_product_id) values ('DISCOUNT', now(), now(), 100, 3, 6, null);
insert into event (event_type, created_at, modified_at, discount_amount, convenience_store_id, product_id, gift_product_id) values ('DISCOUNT', now(), now(), 100, 3, 7, null);
insert into event (event_type, created_at, modified_at, discount_amount, convenience_store_id, product_id, gift_product_id) values ('DISCOUNT', now(), now(), 100, 3, 10, null);

-- tag
insert into tag (name, tag_group, created_at, modified_at) values ('맵찔이', 1, now(), now()), ('맵부심', 1, now(), now()), ('초코러버', 2, now(), now()), ('비건', 3, now(), now()), ('다이어터', 4, now(), now()), ('대식가', 5, now(), now()), ('소식가', 5, now(), now());