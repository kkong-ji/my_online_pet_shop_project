package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest         // 통합 테스트를 위해 스프링 부트에서 제공하는 어노테이션
@TestPropertySource(locations = "classpath:application-test.properties")       // 테스트 코드 실행시 application-test.properties에 같은 설정이 있다면 더 높은 우선순위 부여
class ItemRepositoryTest {

    @PersistenceContext                 // 영속성 컨텍스트 사용하기 위해
    EntityManager em;                   // EntityManager 빈 주입

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

    public void createItemList() {                  // 테스트 코드 실행 시 DB에 상품 데이터가 없으므로 데이터 생성
        for(int i=1; i<=10; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100); item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");
        for(Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByItemNmOrItemDetailTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        for(Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDesc() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for(Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for(Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("Querydsl 조회 테스트1")
    public void queryDslTest() {
        this.createItemList();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);     // JPAQueryFactory를 이용하여 쿼리를 동적으로 생성.
        QItem qItem = QItem.item;                                   // Querydsl을 통해 쿼리를 생성하기 위해 플러그인을 통해 자동으로 생성된 QItem 객체 이용
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)       // 자바 소스코드지만 SQL문과 비슷하게 소스를 작성할 수 있음
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))
                .orderBy(qItem.price.desc());

        List<Item> itemList = query.fetch();                        // JPAQuery 메소드 중 하나인 fetch를 이용해서 쿼리 결과를 리스트로 반환

        for(Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    public void createItemList2() {
        for(int i=1; i<=5; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }

        for(int i=6; i<10; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }

    }

    @Test
    @DisplayName("상품 Querydsl 조회 테스트 2")
    public void queryDslTest2() {

        this.createItemList2();

        BooleanBuilder booleanBuilder = new BooleanBuilder();       // booleanBuilder는 쿼리에 들어갈 조건을 만들어주는 빌더
        QItem item = QItem.item;

        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        String itemSellStat = "SELL";

        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%"));   // 필요한 상품을 조회하는데 필요한 "and" 조건 추가
        booleanBuilder.and(item.price.gt(price));

        if (StringUtils.equals(itemSellStat, ItemSellStatus.SELL)) {            // 상품의 판매상태가 SELL일 때만 booleanBuilder에 판매상태 조건을 동적으로 추가
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        Pageable pageable = PageRequest.of(0, 5);                     // 데이터를 페이징해 조회하도록 PageRequest.of() 메소드를 이용. 첫 번째 인자는 조회할 페이지의 번호, 두 번째 인자는 한 페이지당 조회할 데이터의 개수
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable);   // findAll() 메소드를 이용해 조건에 맞는 데이터를 Page 객체로 받아옴
        System.out.println("total elements : " + itemPagingResult. getTotalElements());

        List<Item> resultItemList = itemPagingResult.getContent();
        for(Item resultItem: resultItemList) {
            System.out.println(resultItem.toString());
        }

    }

}