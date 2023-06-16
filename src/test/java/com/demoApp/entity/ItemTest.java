package com.demoApp.entity;

import com.demoApp.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ItemTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    public void save(){
        //id자동 생성 안 해서 수동으로 id setting하는 경우
        //persist()호출 안 되고 merge()호출됨
        Item item = new Item(1L);
        itemRepository.save(item);
    }

}