-- category
insert into category (name, created_at, modified_at) values ('간편식사', now(), now()), ('즉석요리', now(), now()), ('과자&빵', now(), now()), ('아이스크림', now(), now()), ('신선식품', now(), now()), ('유제품', now(), now()), ('음료', now(), now()), ('기타', now(), now());

-- convenience_store
insert into convenience_store (name, created_at, modified_at) values ('CU', now(), now()), ('GS25', now(), now()), ('세븐일레븐', now(), now()), ('Emart24', now(), now()), ('미니스톱', now(), now());

-- manufacturer
insert into manufacturer (name, created_at, modified_at) values ('오뚜기', now(), now()), ('크라운', now(), now()), ('매일', now(), now()), ('해태', now(), now()), ('농심', now(), now()), ('칠성', now(), now()), ('롯데', now(), now()), ('풀무원', now(), now());

-- product
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at) values ('진라면순한맛', 950, 2, 1, now(), now());
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at) values ('마이쮸포도', 800, 3, 2, now(), now());
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at) values ('마이쮸딸기', 800, 3, 2, now(), now());
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at) values ('킨터초콜렛맥시', 900, 3, 3, now(), now());
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at) values ('칼카야버터', 800, 6, 4, now(), now());
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at) values ('백산수500ml', 950, 7, 5, now(), now());
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at) values ('아이시스500ml', 950, 7, 6, now(), now());
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at) values ('왓따 청포도', 500, 8, 7, now(), now());
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at) values ('왓따 복숭아', 500, 8, 7, now(), now());
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at) values ('풀무원샘물', 950, 7, 8, now(), now());

-- tag
insert into tag (name, tag_group, created_at, modified_at) values ('맵찔이', 1, now(), now()), ('맵부심', 1, now(), now()), ('초코러버', 2, now(), now()), ('비건', 3, now(), now()), ('다이어터', 4, now(), now()), ('대식가', 5, now(), now()), ('소식가', 5, now(), now());