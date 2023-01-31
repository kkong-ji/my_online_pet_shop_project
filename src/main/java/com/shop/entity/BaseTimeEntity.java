package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@EntityListeners(value = {AuditingEntityListener.class})        // Auditing을 적용하기 위해서 @EntityListeners 어노테이션을 추가
@MappedSuperclass                                               // 공통 매핑 정보가 필요할 때 사용하는 어노테이션으로 부모 클래스를 상속 받는
@Getter                                                         // 자식 클래스에 매핑 정보만 제공
@Setter
public abstract class BaseTimeEntity {

    @CreatedDate                                                // 엔티티가 생성되어 저장될 때 시간을 자동으로 저장
    @Column(updatable = false)
    private LocalDateTime regTime;

    @LastModifiedDate                                           // 엔티티의 값을 변경할 때 시간을 자동으로 저장
    private LocalDateTime updateTime;

    @Column(name = "modified_date")
    @LastModifiedDate
    private String modifiedDate;

    /* 해당 엔티티를 업데이트 하기 이전에 실행*/
    @PreUpdate
    public void onPreUpdate(){
        this.modifiedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
    }
}

