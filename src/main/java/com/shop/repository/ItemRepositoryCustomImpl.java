package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.dto.QMainItemDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import com.shop.entity.QItemImg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    private JPAQueryFactory queryFactory;

    public ItemRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    private BooleanExpression regDtsAfter(String searchDateType){

        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        } else if(StringUtils.equals("1d", searchDateType)){
            dateTime = dateTime.minusDays(1);
        } else if(StringUtils.equals("1w", searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        } else if(StringUtils.equals("1m", searchDateType)){
            dateTime = dateTime.minusMonths(1);
        } else if(StringUtils.equals("6m", searchDateType)){
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery){

        if(StringUtils.equals("itemNm", searchBy)){
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("createdBy", searchBy)){
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }

        return null;
    }

    private BooleanExpression searchByCategory(String searchItemCategory, String searchQuery){

        if(StringUtils.equals("[의류]", searchItemCategory)){
            return QItem.item.itemCategory.like("[의류]" + searchQuery);
        } else if(StringUtils.equals("[강아지 용품]", searchItemCategory)){
            return QItem.item.itemCategory.like("[강아지 용품]" + searchQuery);
        } else if(StringUtils.equals("[장난감]", searchItemCategory)) {
            return QItem.item.itemCategory.like("[장난감]" + searchQuery);
        } else if(StringUtils.equals("[사료(껌)]", searchItemCategory)) {
            return QItem.item.itemCategory.like("[사료(껌)]" + searchQuery);
        } else if(StringUtils.equals("[기타]", searchItemCategory)) {
            return QItem.item.itemCategory.like("[기타]" + searchQuery);
        }
        return null;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        List<Item> content = queryFactory
                .selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByCategory(itemSearchDto.getSearchItemCategory(), itemSearchDto.getSearchQuery()),
                        searchByLike(itemSearchDto.getSearchBy(),
                                itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(Wildcard.count).from(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByCategory(itemSearchDto.getSearchItemCategory(), itemSearchDto.getSearchQuery()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .fetchOne()
                ;

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanBuilder itemNmLike(String searchQuery){
        BooleanBuilder builder = new BooleanBuilder();

        builder.or(QItem.item.itemNm.like("%" + searchQuery + "%"));
        return builder;
    }

    private BooleanBuilder itemCategoryLike(String searchQuery){
        BooleanBuilder builder = new BooleanBuilder();

        builder.or(QItem.item.itemCategory.like("%" + searchQuery + "%"));
        return builder;
    }


    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> content = queryFactory
                .select(
                        new QMainItemDto(
                                item.id,
                                item.itemNm,
                                item.itemCategory,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery())
                        .or(itemCategoryLike(itemSearchDto.getSearchQuery())))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery())
                        .or(itemCategoryLike(itemSearchDto.getSearchQuery())))
                .fetchOne()
                ;

        return new PageImpl<>(content, pageable, total);
    }

}