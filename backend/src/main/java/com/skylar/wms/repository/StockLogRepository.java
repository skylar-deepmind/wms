package com.skylar.wms.repository;

import com.skylar.wms.entity.StockLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockLogRepository extends JpaRepository<StockLog,Long> {
    List<StockLog> findByItemIdOrderByCreateTimeDesc(Long itemId);
}
