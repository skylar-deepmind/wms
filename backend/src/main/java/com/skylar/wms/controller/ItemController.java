package com.skylar.wms.controller;

import com.skylar.wms.entity.Item;
import com.skylar.wms.entity.StockLog;
import com.skylar.wms.repository.ItemRepository;
import com.skylar.wms.repository.StockLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*") // 允许前端跨域访问
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private StockLogRepository stockLogRepository;

    // 1. 获取所有物资
    @GetMapping
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // 2. 新增物资
    @PostMapping
    public Item addItem(@RequestBody Item item) {
        return itemRepository.save(item);
    }

    // 3. 删除物资
    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        itemRepository.deleteById(id);
    }

    // 4. 修改物资 (简单实现)
    @PutMapping
    public Item updateItem(@RequestBody Item item) {
        return itemRepository.save(item);
    }

    // 5.入库
    @PostMapping("/{id}/inbound")
    public Item inbound(@PathVariable Long id, @RequestParam Integer count) {

        //先把要修改的item查出来
        Item item = itemRepository.findById(id).orElseThrow(() ->
                new RuntimeException("该物资不存在！"));

        //入库
        item.setQuantity(item.getQuantity() + count);

        //log
        recordLog(id,"入库",count);

        //保存并且返回
        return itemRepository.save(item);
    }

    // 6.出库
    @PostMapping("/{id}/outbound")
    public Item outbound(@PathVariable Long id, @RequestParam Integer count) {

        //先把要修改的item查出来
        Item item = itemRepository.findById(id).orElseThrow(() ->
                new RuntimeException("该物资不存在！"));

        //库存检查，避免为负
        if (item.getQuantity() - count < 0) {
            throw new RuntimeException("库存不足，当前剩余：" + item.getQuantity());
        }

        //出库
        item.setQuantity(item.getQuantity() - count);

        //log
        recordLog(id,"出库",count);

        //保存并且返回
        return itemRepository.save(item);
    }

    // 日志查询
    @GetMapping("/{id}/logs")
    public List<StockLog> getItemLogs(@PathVariable Long id){
        return stockLogRepository.findByItemIdOrderByCreateTimeDesc(id);
    }

    // 辅助方法：封装记账代码，避免重复
    private void recordLog(Long itemId, String type, Integer count){
        StockLog log = new StockLog();
        log.setItemId(itemId);
        log.setType(type);
        log.setCount(count);

        stockLogRepository.save(log);
    }
}