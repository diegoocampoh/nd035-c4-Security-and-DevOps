package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Before
    public void setup() {
    }

    @Test
    public void createUser(){
        given(bCryptPasswordEncoder.encode(any())).willReturn("encodedPassword");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("diego");
        request.setPassword("diego12345");
        request.setConfirmPassword("diego12345");

        ResponseEntity<User> response = userController.createUser(request);
        User responseUser = response.getBody();
        Assert.assertEquals("diego", responseUser.getUsername());
        Assert.assertEquals(0, responseUser.getId());
        Assert.assertEquals("encodedPassword", responseUser.getPassword());
    }

    @Test
    public void createUserShouldFailIfPasswordDoesntMeetConditions(){
        given(bCryptPasswordEncoder.encode(any())).willReturn("encodedPassword");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("diego");
        request.setPassword("diego");
        request.setConfirmPassword("diego");

        ResponseEntity<User> response = userController.createUser(request);
        Assert.assertEquals(400, response.getStatusCodeValue());
    }


}
