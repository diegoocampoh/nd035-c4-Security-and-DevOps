package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class ItemControllerTest {

    @Autowired
    private ItemController itemController;

    @MockBean
    private ItemRepository itemRepository;

    private static List<Item> itemsList;

    @BeforeClass
    public static void setUp() {
        itemsList = new ArrayList<>();
        itemsList.add(new Item(1L, "Book", BigDecimal.valueOf(100), "A book"));
        itemsList.add(new Item(2L, "Apple", BigDecimal.valueOf(5), "Granny smith"));
        itemsList.add(new Item(3L, "Mug", BigDecimal.valueOf(20), "Coffee mug"));

    }

    @Test
    public void testGetById() {
        given(itemRepository.findById(1l))
                .willReturn(Optional
                        .of(new Item(1L, "Book", BigDecimal.valueOf(100), "A book")));
        ResponseEntity<Item> response = itemController.getItemById(1l);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Book", response.getBody().getName());
    }

    @Test
    public void testGetItems() {
        given(itemRepository.findAll()).willReturn(itemsList);
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(itemsList, response.getBody());
    }

    @Test
    public void testItemsByName_existing() {
        given(itemRepository.findByName("Book"))
                .willReturn(List.of(new Item(1L, "Book", BigDecimal.valueOf(100), "A book")));
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Book");
        assertEquals(200, response.getStatusCodeValue());
        response.getBody().forEach(item -> {
                    assertEquals("Book", item.getName());
                    Assert.assertEquals(1L, item.getId().longValue());
                }
        );
    }

    @Test
    public void testItemsByName_non_existing() {
        given(itemRepository.findByName("Book")).willReturn(emptyList());
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Book");
        assertEquals(404, response.getStatusCodeValue());
    }

}
