-- category
insert into category (name, created_at, modified_at) values ('간편식사', now(), now()), ('즉석요리', now(), now()), ('과자&빵', now(), now()), ('아이스크림', now(), now()), ('신선식품', now(), now()), ('유제품', now(), now()), ('음료', now(), now()), ('기타', now(), now());

-- convenience_store
insert into convenience_store (name, created_at, modified_at) values ('CU', now(), now()), ('GS25', now(), now()), ('세븐일레븐', now(), now()), ('Emart24', now(), now()), ('미니스톱', now(), now());

-- manufacturer
insert into manufacturer (name, created_at, modified_at) values ('오뚜기', now(), now()), ('크라운', now(), now()), ('매일', now(), now()), ('해태', now(), now()), ('농심', now(), now()), ('칠성', now(), now()), ('롯데', now(), now()), ('풀무원', now(), now());
insert into manufacturer (name, created_at, modified_at) values('유어스', now(), now());
insert into manufacturer (name, created_at, modified_at) values('HEYROO', now(), now());
insert into manufacturer (name, created_at, modified_at) values('서울우유', now(), now());
insert into manufacturer (name, created_at, modified_at) values('빙그레', now(), now());
insert into manufacturer (name, created_at, modified_at) values('삼각', now(), now());
insert into manufacturer (name, created_at, modified_at) values('주', now(), now());
insert into manufacturer (name, created_at, modified_at) values('도', now(), now());

-- notice
insert into notice (title, content, created_at, modified_at) values('편해 출시', '편의점 리뷰 서비스 편해가 출시되었습니다.', now(), now());

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
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('틈새라면', 1350, 2, 9, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('콘소메맛팝콘', 1700, 3, 10, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('비요뜨초코링', 1800, 6, 11, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('비요뜨링크', 1800, 6, 11, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('비요뜨쿠키앤크림', 1800, 6, 11, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('커피우유200ML', 1100, 6, 11, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('커피우유300ML', 1800, 6, 11, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('초코우유200ML', 1100, 6, 11, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('초코우유300ML', 1800, 6, 11, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('딸기우유200ML', 1100, 6, 11, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('딸기우유300ML', 1800, 6, 11, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('우유200ML', 1100, 6, 11, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('우유500ML', 2000, 6, 11, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('우유1L', 3050, 6, 11, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('바나나우유240ML', 1700, 6, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('바나나우유라이트240ML', 1700, 6, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('딸기우유240ML', 1700, 6, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('따옴사과주스235ML', 2200, 7, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('따옴쥬스사과730ml', 3500, 7, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('따옴쥬스자몽730ml', 3500, 7, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('따옴쥬스자몽220ml', 1500, 7, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('따옴쥬스오렌지730ml', 3500, 7, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('쥬시쿨자두180ML', 500, 7, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('아카페라사이즈업라떼', 2400, 7, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('아카페라마끼야또240ML', 2000, 7, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('아카페라아메리카노240ML', 2000, 7, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('따옴오렌지주스235ML', 2200, 7, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('떡붕어싸만코', 2200, 4, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('부드러운빵또아', 2200, 4, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('슈퍼콘초코', 2200, 4, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('슈퍼콘프렌치바닐라', 2200, 4, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('더위사냥', 1800, 4, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('요맘때바', 1500, 4, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('쿠앤크바', 1500, 4, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('비비빅', 1500, 4, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('투게더바닐라', 9000, 4, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('엑설런트', 10000, 4, 12, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('엑설런트콘', 2500, 4, 12, now(), now());
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('스팸참치마요김차볶음밥1편', 1100, 1, 13, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('명란마요1편', 1100, 1, 13, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('명란마요2편', 1100, 1, 13, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('참치마요1편', 1000, 1, 13, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('참치마요2편', 1000, 1, 13, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('전주비빔1편', 1000, 1, 13, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('전주비빔2편', 1000, 1, 13, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('비빔참치1편', 1000, 1, 13, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('비빔참치2편', 1000, 1, 13, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('매콤참치1편', 1100, 1, 13, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('매콤참치2편', 1100, 1, 13, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('스팸계란볶음밥1편', 1100, 1, 13, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('스팸계란볶음밥2편', 1100, 1, 13, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('치킨마요삼각김밥1', 1200, 1, 14, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('치킨마요삼각김밥2', 1200, 1, 14, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('2XL베이컨참치마요1', 1500, 1, 14, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('2XL베이컨참치마요2', 1500, 1, 14, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('2XL스팸마요1', 1500, 1, 14, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('2XL스팸마요2', 1500, 1, 14, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('3XL리치치킨데리마요2', 1500, 1, 14, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('3XL리치소고기전주1', 1500, 1, 14, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('3XL리치소고기전주2', 1500, 1, 14, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('백종원트리플포크삼각1', 1100, 1, 14, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('백종원트리플포크삼각2', 1100, 1, 14, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('백종원2XL치즈제육1', 1500, 1, 14, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('백종원2XL치즈제육2', 1500, 1, 14, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('백종원떡갈비불고기1', 1700, 1, 14, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('백종원떡갈비불고기2', 1700, 1, 14, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('백종원제육한판1', 4500, 1, 15, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('백종원제육한판2', 4500, 1, 15, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('백종원고기2배정식2', 4800, 1, 15, now(), now(), 0);
insert into product (name, price, category_id, manufacturer_id, created_at, modified_at, version) values ('백종원더블까스정식2', 5000, 1, 15, now(), now(), 0);

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
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (11, 2, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (12, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (13, 1, now(), now()), (13, 2, now(), now()), (13, 3, now(), now()), (13, 4, now(), now()), (13, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (14, 1, now(), now()), (14, 2, now(), now()), (14, 3, now(), now()), (14, 4, now(), now()), (14, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (15, 1, now(), now()), (15, 2, now(), now()), (15, 3, now(), now()), (15, 4, now(), now()), (15, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (16, 1, now(), now()), (16, 2, now(), now()), (16, 3, now(), now()), (16, 4, now(), now()), (16, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (17, 1, now(), now()), (17, 2, now(), now()), (17, 3, now(), now()), (17, 4, now(), now()), (17, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (18, 1, now(), now()), (18, 2, now(), now()), (18, 3, now(), now()), (18, 4, now(), now()), (18, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (19, 1, now(), now()), (19, 2, now(), now()), (19, 3, now(), now()), (19, 4, now(), now()), (19, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (20, 1, now(), now()), (20, 2, now(), now()), (20, 3, now(), now()), (20, 4, now(), now()), (20, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (21, 1, now(), now()), (21, 2, now(), now()), (21, 3, now(), now()), (21, 4, now(), now()), (21, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (22, 1, now(), now()), (22, 2, now(), now()), (22, 3, now(), now()), (22, 4, now(), now()), (22, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (23, 1, now(), now()), (23, 2, now(), now()), (23, 3, now(), now()), (23, 4, now(), now()), (23, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (24, 1, now(), now()), (24, 2, now(), now()), (24, 3, now(), now()), (24, 4, now(), now()), (24, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (25, 1, now(), now()), (25, 2, now(), now()), (25, 3, now(), now()), (25, 4, now(), now()), (25, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (26, 1, now(), now()), (26, 2, now(), now()), (26, 3, now(), now()), (26, 4, now(), now()), (26, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (27, 1, now(), now()), (27, 2, now(), now()), (27, 3, now(), now()), (27, 4, now(), now()), (27, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (28, 1, now(), now()), (28, 2, now(), now()), (28, 3, now(), now()), (28, 4, now(), now()), (28, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (29, 1, now(), now()), (29, 2, now(), now()), (29, 3, now(), now()), (29, 4, now(), now()), (29, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (30, 1, now(), now()), (30, 2, now(), now()), (30, 3, now(), now()), (30, 4, now(), now()), (30, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (31, 1, now(), now()), (31, 2, now(), now()), (31, 3, now(), now()), (31, 4, now(), now()), (31, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (32, 1, now(), now()), (32, 2, now(), now()), (32, 3, now(), now()), (32, 4, now(), now()), (32, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (33, 1, now(), now()), (33, 2, now(), now()), (33, 3, now(), now()), (33, 4, now(), now()), (33, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (34, 1, now(), now()), (34, 2, now(), now()), (34, 3, now(), now()), (34, 4, now(), now()), (34, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (35, 1, now(), now()), (35, 2, now(), now()), (35, 3, now(), now()), (35, 4, now(), now()), (35, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (36, 1, now(), now()), (36, 2, now(), now()), (36, 3, now(), now()), (36, 4, now(), now()), (36, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (37, 1, now(), now()), (37, 2, now(), now()), (37, 3, now(), now()), (37, 4, now(), now()), (37, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (38, 1, now(), now()), (38, 2, now(), now()), (38, 3, now(), now()), (38, 4, now(), now()), (38, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (39, 1, now(), now()), (39, 2, now(), now()), (39, 3, now(), now()), (39, 4, now(), now()), (39, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (40, 1, now(), now()), (40, 2, now(), now()), (40, 3, now(), now()), (40, 4, now(), now()), (40, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (41, 1, now(), now()), (41, 2, now(), now()), (41, 3, now(), now()), (41, 4, now(), now()), (41, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (42, 1, now(), now()), (42, 2, now(), now()), (42, 3, now(), now()), (42, 4, now(), now()), (42, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (43, 1, now(), now()), (43, 2, now(), now()), (43, 3, now(), now()), (43, 4, now(), now()), (43, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (44, 1, now(), now()), (44, 2, now(), now()), (44, 3, now(), now()), (44, 4, now(), now()), (44, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (45, 1, now(), now()), (45, 2, now(), now()), (45, 3, now(), now()), (45, 4, now(), now()), (45, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (46, 1, now(), now()), (46, 2, now(), now()), (46, 3, now(), now()), (46, 4, now(), now()), (46, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (47, 1, now(), now()), (47, 2, now(), now()), (47, 3, now(), now()), (47, 4, now(), now()), (47, 5, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (48, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (49, 2, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (50, 2, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (51, 2, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (52, 2, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (53, 2, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (54, 2, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (55, 2, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (56, 2, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (57, 2, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (58, 2, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (59, 2, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (60, 2, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (61, 2, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (62, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (63, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (64, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (65, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (66, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (67, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (68, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (69, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (70, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (71, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (72, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (73, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (74, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (75, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (76, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (77, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (78, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (79, 1, now(), now());
insert into sell_at (product_id, convenience_store_id, created_at, modified_at) values (80, 1, now(), now());

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
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 1, 13);
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 1, 14);
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 1, 15);
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 1, 17);
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 1, 19);
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 1, 21);
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 2, 17);
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 2, 19);
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 2, 21);
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 1, 38);
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 1, 39);
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 1, 40);
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 1, 41);
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 1, 45);
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 2, 34);
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 2, 35);
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 2, 36);
insert into event (event_type, created_at, modified_at, discount_amount, convenience_store_id, product_id) values ('DISCOUNT', now(), now(), 800, 1, 77);
insert into event (event_type, created_at, modified_at, discount_amount, convenience_store_id, product_id) values ('DISCOUNT', now(), now(), 800, 1, 78);
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 2, 13);
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 2, 14);
insert into event (event_type, created_at, modified_at, convenience_store_id, product_id) values ('BTGO', now(), now(), 2, 15);

-- tag
insert into tag (name, tag_group, created_at, modified_at) values ('맵찔이', 1, now(), now()), ('맵부심', 1, now(), now()), ('초코러버', 2, now(), now()), ('비건', 3, now(), now()), ('다이어터', 4, now(), now()), ('대식가', 5, now(), now()), ('소식가', 5, now(), now());