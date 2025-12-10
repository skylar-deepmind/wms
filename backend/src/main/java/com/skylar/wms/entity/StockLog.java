package com.skylar.wms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class StockLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long itemId; // 关联物资

    private String type; // 入库/出库

    private Integer count; // 变动数量

    private LocalDateTime createTime = LocalDateTime.now(); // 记录时间
}
