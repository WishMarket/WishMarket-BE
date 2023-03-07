package com.zerobase.wishmarket.domain.alarm.model.entity;

import com.zerobase.wishmarket.common.entity.BaseEntity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Alarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contents;
    private boolean alarmStatus;
    private Long userId;

    public void setAsRead() {
        this.alarmStatus = true;
    }
}
