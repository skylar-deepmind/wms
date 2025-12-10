package com.skylar.wms.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data // Lombok 注解，自动生成 Getter/Setter
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;      // 货物名称
    private Integer quantity; // 数量
    private String remark;    // 备注
}
