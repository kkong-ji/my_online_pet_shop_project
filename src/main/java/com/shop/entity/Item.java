package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.exception.OutOfStockException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity                                                 // Item 클래스를 entity로 설정
@Table(name="item")                                     // Item 테이블과 매핑되도록 name을 item으로 지정
@Getter
@Setter
@ToString
public class Item extends BaseEntity {

    @Id                                                 // 기본키
    @Column(name="item_id")                             // 테이블에 매핑될 컬럼 이름
    @GeneratedValue(strategy = GenerationType.AUTO)     // 기본키 생성 전략
    private Long id;                                    // 상품 코드

    @Column(nullable = false, length = 50)
    private String itemNm;                              // 상품명

    @Column(nullable = false)
    private String itemCategory;                  // 아이템 카테고리

    @Column(name="price", nullable = false)
    private int price;                                  // 가격

    @Column(nullable = false)
    private int stockNumber;                            // 재고 수량

    @Lob
    @Column(nullable = false)
    private String itemDetail;                          // 상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;              // 상품 판매 상태

    public void updateItem(ItemFormDto itemFormDto) {
        this.itemNm = itemFormDto.getItemNm();
        this.itemCategory = itemFormDto.getItemCategory();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    public void removeStock(int stockNumber) {
        int restStock = this.stockNumber - stockNumber;
        if(restStock < 0) {
            throw new OutOfStockException("상품의 재고가 부족합니다." +
                    "(현재 재고 수량: " + this.stockNumber + ")");
        }
        this.stockNumber = restStock;
    }

    public void addStock(int stockNumber) {
        this.stockNumber += stockNumber;
    }
}
