package kz.epam.newsportal.controller;

import kz.epam.newsportal.model.News;
import kz.epam.newsportal.model.Role;
import kz.epam.newsportal.model.User;
import kz.epam.newsportal.repository.INewsRepository;
import kz.epam.newsportal.repository.IUserRepository;
import kz.epam.newsportal.repository.hibernate.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:context/spring-mvc.xml","classpath:context/spring-root.xml", "classpath:context/spring-security.xml",
        "classpath:context/spring-repository.xml"})
@Transactional
public class UserControllerTest {

    private MockMvc mockMvc;

    @Autowired
    IUserRepository userRepository;

    @Autowired
    private UserController userController;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void shouldReturnUsersList() throws Exception {
        createMockUsers(5);

        ResultActions resultActions = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", Matchers.hasSize(5)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].email", is("user1@mail.kz")))
                .andExpect(jsonPath("$[4].id", is(4)))
                .andExpect(jsonPath("$[4].email", is("user4@mail.kz")));

    }

    // Helper methods
    private void createMockUsers(int count) {
        for (int i = 1; i <= count; i++) {
            User user = new User("user" + i + "@mail.kz", "user"+i, "Name: " + i, true, true, true, true);
            Role role = new Role("ADMIN_ROLE");
            user.addRole(role);
            user.setId((long) i);
            userRepository.save(user);
        }
    }
}
