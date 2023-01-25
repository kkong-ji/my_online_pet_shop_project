package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {

    List<Item> findByItemNm(String itemNm);     // 데이터 조회 쿼리 메소드
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);  // 상품을 상품명과 상품 상세 설명을 OR 조건을 이용하여 조회하는 쿼리 메소드
    List<Item> findByPriceLessThan(Integer price);
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    @Query(value = "select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc", nativeQuery = true)  // @Query 어노테이션 안에 JPQL로 작성한 쿼리를 넣어줌.
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);    // 파라미터에 @Param 어노테이션을 이용하여 파라미터로 넘어온 값을 JPQL에 들어갈 변수로 지정해줌.
}
