package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CartControllerTest {

    @Autowired
    private CartController cartController;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private ItemRepository itemRepository;

    private static User user;
    private static Cart cart;
    private static Item item;

    @BeforeClass
    public static void setUp() {
        cart = new Cart();
        user = new User();
        item = new Item();

        user.setUsername("diego");
        user.setCart(cart);
        cart.setUser(user);

        item.setId(12345L);
        item.setPrice(BigDecimal.valueOf(105));
    }

    @Test
    public void testAddToCart() {

        given(userRepository.findByUsername("diego")).willReturn(user);
        given(itemRepository.findById(12345L)).willReturn(Optional.of(item));
        given(cartRepository.save(any())).willReturn(cart);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(12345L);
        request.setQuantity(1);
        request.setUsername("diego");

        ResponseEntity<Cart> response = cartController.addTocart(request);
        Cart responseCart = response.getBody();

        assertNotNull(responseCart);
        assertEquals(BigDecimal.valueOf(105), responseCart.getTotal());
        assertEquals("diego", responseCart.getUser().getUsername());
        assertTrue(responseCart.getItems().contains(item));
    }

    @Test
    public void testRemoveFromCart() {

        Item item = new Item(123l, "Book", BigDecimal.valueOf(100), "");
        Cart cart = new Cart();
        cart.addItem(item);
        cart.setUser(user);
        user.setCart(cart);

        given(userRepository.findByUsername("diego")).willReturn(user);
        given(itemRepository.findById(123l)).willReturn(Optional.of(item));
        given(cartRepository.save(any())).willReturn(cart);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(123l);
        request.setQuantity(1);
        request.setUsername("diego");

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        Cart responseCart = response.getBody();

        assertNotNull(responseCart);
        assertEquals(BigDecimal.valueOf(0), responseCart.getTotal());
        assertEquals("diego", responseCart.getUser().getUsername());
        assertFalse(responseCart.getItems().contains(item));
    }

    @Test
    public void testUserNonExistent_add() {
        given(userRepository.findByUsername(any())).willReturn(null);
        ModifyCartRequest request = new ModifyCartRequest();
        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testItemNonExistent_add() {
        given(itemRepository.findById(any())).willReturn(null);
        ModifyCartRequest request = new ModifyCartRequest();
        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testUserNonExistent_remove() {
        given(userRepository.findByUsername(any())).willReturn(null);
        ModifyCartRequest request = new ModifyCartRequest();
        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testItemNonExistent_remove() {
        given(itemRepository.findById(any())).willReturn(null);
        ModifyCartRequest request = new ModifyCartRequest();
        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertEquals(404, response.getStatusCodeValue());
    }



}
