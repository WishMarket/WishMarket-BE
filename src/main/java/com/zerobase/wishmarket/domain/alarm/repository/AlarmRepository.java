package com.zerobase.wishmarket.domain.alarm.repository;

import com.zerobase.wishmarket.domain.alarm.model.Alarm;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findAllByUserId(Long userId);

    int countByUserIdAndIsReadFalse(Long userId);
}
