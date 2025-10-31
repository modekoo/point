package com.musinsa.point;

import com.musinsa.point.entity.User;
import com.musinsa.point.entity.UserPointInfo;
import com.musinsa.point.repository.UserPointRepository;
import com.musinsa.point.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserTests {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserPointRepository userPointRepository;

	@Test
	void createUserAndUserPointInfo() throws Exception {

        String reqJsonStr = """
                {"userId":"koo"}
                """;

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqJsonStr)
        ).andExpect(status().isOk());

        Optional<User> user = userRepository.findByUserId("koo");
        if(user.isEmpty()) Assertions.fail();
        Assertions.assertEquals("koo", user.get().getUserId());

        Optional<UserPointInfo> userPointInfo = userPointRepository.findById(user.get().getUserKey());
        if(userPointInfo.isEmpty()) Assertions.fail();

	}



}
