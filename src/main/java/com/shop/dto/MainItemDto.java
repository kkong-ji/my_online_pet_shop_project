package com.shop.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainItemDto {

    private Long id;

    private String itemNm;

    private String itemCategory;

    private String itemDetail;

    private String imgUrl;

    private Integer price;

    @QueryProjection                        // 생성자에 @QueryProjection 어노테이션을 선언하여 Querydsl로 결과 조회 시 MainItemDto 객체로 바로 받아옴
    public MainItemDto(Long id, String itemNm, String itemCategory, String itemDetail, String imgUrl, Integer price) {
        this.id = id;
        this.itemNm = itemNm;
        this.itemCategory = itemCategory;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
    }
}
