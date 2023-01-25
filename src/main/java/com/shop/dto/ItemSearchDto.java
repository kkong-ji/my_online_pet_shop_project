package com.shop.dto;


import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemSearchDto {

    private String searchDateType;

    private ItemSellStatus searchSellStatus;        // 상품의 판매상태를 기준으로 상품 데이터 조회

    private String searchBy;                        // 상품 조회시 어떤 유형으로 조회할지 선택
                                                    // itemNm : 상품명, createdBy : 상품 등록자 아이디
    private String searchQuery = "";                // 조회할 검색어 저장할 변수
}
