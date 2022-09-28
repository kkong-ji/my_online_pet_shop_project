package com.shop.repository;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest         // 통합 테스트를 위해 스프링 부트에서 제공하는 어노테이션
@TestPropertySource(locations = "classpath:application-test.properties")       // 테스트 코드 실행시 application-test.properties에 같은 설정이 있다면 더 높은 우선순위 부여
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;      // ItemRepository를 사용하기 위해 @Autowired 어노테이션을 이용해 Bean 주입

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem.toString());
    }

}