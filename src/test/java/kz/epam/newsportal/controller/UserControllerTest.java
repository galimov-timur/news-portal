package kz.epam.newsportal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.epam.newsportal.model.Role;
import kz.epam.newsportal.model.User;
import kz.epam.newsportal.repository.IUserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ContextConfiguration(locations = {"classpath:context/spring-mvc.xml",
        "classpath:context/spring-root.xml", "classpath:context/spring-security.xml",
        "classpath:context/spring-repository.xml"})

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
    public void getUsersShouldReturnUsersList() throws Exception {
        // Setup mock users
        createMockUsers(2);
        // Perform get request
        mockMvc.perform(get("/users"))
                // Validate response code, content type and list size
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                // Validate returned fields
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].email", is("user1@mail.kz")))
                .andExpect(jsonPath("$[0].username", is("Name: 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].email", is("user2@mail.kz")))
                .andExpect(jsonPath("$[1].username", is("Name: 2")));

    }

    @Test
    public void getUserByIdShouldReturnUser() throws Exception {
        // Setup mock users
        createMockUsers(1);
        // Perform get request
        mockMvc.perform(get("/users/{id}", 1))
                // Validate status and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                // Validate returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("user1@mail.kz")))
                .andExpect(jsonPath("$.username", is("Name: 1")));
    }

    @Test
    public void getUserByIdShouldReturnNotFound() throws Exception {
        // Perform get request
        mockMvc.perform(get("/users/{id}", 1))
                // Validate status
                .andExpect(status().isNotFound());
    }

    @Test
    public void addUserShouldReturnCreatedAndLocationHeader() throws Exception {
        // Create mock user
        User user = new User("user@mail.kz", "user", "Name", true, true, true, true);
        Role role = new Role("USER_ROLE");
        user.addRole(role);
        // Execute the post request
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                // Validate status
                .andExpect(status().isCreated())
                // Validate headers
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/users/1"));
    }

    @Test
    public void updateUserShouldReturnNoContentStatus() throws Exception {
        // Setup mock users
        createMockUsers(2);
        // Create updated mock user
        User user = new User("user@mail.kz", "user", "Name", true, true, true, true);
        Role role = new Role("USER_ROLE");
        user.addRole(role);
        // Execute the post request
        mockMvc.perform(put("/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                // Validate status
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateUserShouldReturnNotFoundStatus() throws Exception {
        // Create updated mock user
        User user = new User("user@mail.kz", "user", "Name", true, true, true, true);
        Role role = new Role("USER_ROLE");
        user.addRole(role);
        // Execute the post request
        mockMvc.perform(put("/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                // Validate status
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteUserShouldReturnNoContentStatus() throws Exception {
        // Setup mock users
        createMockUsers(2);
        // Execute delete request
        mockMvc.perform(delete("/users/{id}", 1))
                // Validate status
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteUserShouldReturnNotFoundStatus() throws Exception {
        // Execute delete request
        mockMvc.perform(delete("/users/{id}", 1))
                // Validate status
                .andExpect(status().isNotFound());
    }

    // Helper methods
    private void createMockUsers(int count) {
        for (int i = 1; i <= count; i++) {
            User user = new User("user" + i + "@mail.kz", "user"+i, "Name: " + i, true, true, true, true);
            Role role = new Role("USER_ROLE");
            user.addRole(role);
            user.setId((long) i);
            userRepository.save(user);
        }
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
