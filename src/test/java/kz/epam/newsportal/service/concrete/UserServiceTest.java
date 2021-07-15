package kz.epam.newsportal.service.concrete;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import kz.epam.newsportal.model.User;
import kz.epam.newsportal.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {
    @Mock
    IUserRepository userRepository;
    @InjectMocks
    UserService userService;

    User testUser;

    @BeforeEach
    void setup() {
        long id = 1;
        testUser = new User("test@test.ru", "pass", "Test", true, true, true, true);
        testUser.setId(id);
    }

    @Test
    void testLoadUserByUsername() {
        // given
        String email = "test@test.ru";
        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(testUser));
        // when
        User user = (User) userService.loadUserByUsername(email);
        // then
        assertNotNull(user);
        assertTrue(user.getUsername().equals("Test"));
        assertTrue(user.getPassword().equals("pass"));
        assertTrue(user.getEmail().equals(email));
    }

    @Test
    void testLoadUserByUsernameThrowsUsernameNotFoundException() {
        String email = "test@test.ru";
        User storedUser = null;
        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(Optional.ofNullable(storedUser));
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
    }

    @Test
    void testAddUser() {
        // given
        Optional<User> noUser = Optional.ofNullable(null);
        Mockito.when(userRepository.findUserByEmail(testUser.getEmail())).thenReturn(noUser);
        Mockito.when(userRepository.save(testUser)).thenReturn(testUser.getId());
        // when
        long savedUserId = userService.addUser(testUser);
        // then
        assertTrue(testUser.getId() == savedUserId);
    }

    @Test
    void testGetUsersList() {
        // given
        User testUser2 = new User("test2@test.ru", "pass2", "Test2", true,true,true,true);
        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, testUser2));
        // when
        List<User> usersList = userService.getUsersList();
        // then
        assertTrue(usersList.size() == 2);
        assertTrue(usersList.get(0).getEmail().equals(testUser.getEmail()));
        assertTrue(usersList.get(1).getEmail().equals(testUser2.getEmail()));
    }

    @Test
    void testGetUserById() {
        // given
        long searchedUserId = 1;
        Mockito.when(userRepository.findById(searchedUserId)).thenReturn(testUser);
        // when
        User resultUser = userService.getUserById(searchedUserId);
        // then
        assertTrue(searchedUserId == resultUser.getId());
        assertTrue(resultUser.getEmail().equals(testUser.getEmail()));
    }

    @Test
    void testDeleteUser() {
        // given
        User userToDelete = testUser;
        // when
        userService.deleteUser(userToDelete);
        // then
        verify(userRepository).delete(userToDelete);
    }

    @Test
    void testUpdateUser() throws Exception {
        // given
        User updatedUser = testUser;
        // when
        userService.updateUser(updatedUser);
        // then
        verify(userRepository).update(updatedUser);
    }
}
