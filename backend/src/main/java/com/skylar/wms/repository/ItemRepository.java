package com.skylar.wms.repository;


import com.skylar.wms.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    // JpaRepository 已经帮我们写好了 find, save, delete 等方法，这里不需要写代码
}
