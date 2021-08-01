package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
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
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class OrderControllerTest {

    @Autowired
    private OrderController orderController;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private OrderRepository orderRepository;

    private static User user;
    private static Cart cart;
    private static Item item;

    @BeforeClass
    public static void setUp() {
        cart = new Cart();
        user = new User();
        item = new Item();

        user.setUsername("diego");
        user.setPassword("diego12345");
        user.setCart(cart);
        cart.setUser(user);

        item.setId(12345L);
        item.setPrice(BigDecimal.valueOf(105));
    }

    @Test
    public void testHistoryOrder() {
        cart.addItem(item);
        UserOrder expectedOrder = UserOrder.createFromCart(user.getCart());
        List<UserOrder> orders = List.of(expectedOrder);

        given(userRepository.findByUsername(any())).willReturn(user);
        given(orderRepository.findByUser(any())).willReturn(orders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("diego");
        List<UserOrder> actualOrders = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(orders, actualOrders);
    }

    @Test
    public void testSubmitOrder() {
        given(userRepository.findByUsername(any())).willReturn(user);
        given(orderRepository.save(any())).willReturn(any());
        cart.addItem(item);
        ResponseEntity<UserOrder> response = orderController.submit("diego");
        UserOrder actualOrder = response.getBody();
        UserOrder expectedOrder = UserOrder.createFromCart(user.getCart());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedOrder, actualOrder);
    }


    @Test
    public void testUserNonExistent_submit() {
        given(userRepository.findByUsername(any())).willReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit("diego");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testItemNonExistent_history() {
        given(userRepository.findByUsername(any())).willReturn(null);
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("diego");
        assertEquals(404, response.getStatusCodeValue());
    }


}
