package com.cvsgo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cvsgo.config.TestConfig;
import com.cvsgo.entity.BogoEvent;
import com.cvsgo.entity.BtgoEvent;
import com.cvsgo.entity.ConvenienceStore;
import com.cvsgo.entity.DiscountEvent;
import com.cvsgo.entity.Event;
import com.cvsgo.entity.EventType;
import com.cvsgo.entity.GiftEvent;
import com.cvsgo.entity.Product;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(TestConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EventRepositoryTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ConvenienceStoreRepository convenienceStoreRepository;

    @Test
    @DisplayName("상품 판매 편의점의 행사를 조회한다")
    void succeed_to_find_event_by_product_and_convenience_store() {
        // given
        Product product1 = Product.builder()
            .name("상품1")
            .build();

        Product product2 = Product.builder()
            .name("상품2")
            .build();

        ConvenienceStore cvs1 = ConvenienceStore.builder()
            .name("cvs1")
            .build();

        ConvenienceStore cvs2 = ConvenienceStore.builder()
            .name("cvs2")
            .build();

        ConvenienceStore cvs3 = ConvenienceStore.builder()
            .name("cvs3")
            .build();

        BogoEvent event1 = BogoEvent.builder()
            .product(product1)
            .convenienceStore(cvs1)
            .build();

        BtgoEvent event2 = BtgoEvent.builder()
            .product(product1)
            .convenienceStore(cvs2)
            .build();

        GiftEvent event3 = GiftEvent.builder()
            .product(product2)
            .convenienceStore(cvs1)
            .giftProduct(product1)
            .build();

        DiscountEvent event4 = DiscountEvent.builder()
            .product(product2)
            .convenienceStore(cvs3)
            .discountAmount(500)
            .build();

        productRepository.saveAll(List.of(product1, product2));
        convenienceStoreRepository.saveAll(List.of(cvs1, cvs2, cvs3));
        eventRepository.saveAll(List.of(event1, event2, event3, event4));

        // when
        Event foundEvent1 = eventRepository.findByProductAndConvenienceStore(product1, cvs1);
        Event foundEvent2 = eventRepository.findByProductAndConvenienceStore(product1, cvs2);
        Event foundEvent3 = eventRepository.findByProductAndConvenienceStore(product2, cvs1);
        Event foundEvent4 = eventRepository.findByProductAndConvenienceStore(product2, cvs3);

        // then
        assertThat(foundEvent1.getEventType()).isEqualTo(EventType.BOGO);
        assertThat(foundEvent2.getEventType()).isEqualTo(EventType.BTGO);
        assertThat(foundEvent3.getEventType()).isEqualTo(EventType.GIFT);
        assertThat(foundEvent4.getEventType()).isEqualTo(EventType.DISCOUNT);
        assertThat(foundEvent4.getEventType()).isNotEqualTo(EventType.BOGO);
    }
}
