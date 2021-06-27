package kz.epam.newsportal.service.concrete;

import kz.epam.newsportal.exception.NotFoundException;
import kz.epam.newsportal.exception.UserAlreadyExistException;
import kz.epam.newsportal.model.Role;
import kz.epam.newsportal.model.User;
import kz.epam.newsportal.repository.IUserRepository;
import org.junit.jupiter.api.Assertions;
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

    private static final String ROLE_USER = "USER_ROLE";

    @Mock
    IUserRepository userRepository;
    @InjectMocks
    UserService userService;

    @Test
    void testLoadUserByUsername() {
        String email = "test@test.ru";
        User storedUser = new User(email, "pass", "Test", true, true, true, true);
        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(storedUser));
        User userDetails = (User) userService.loadUserByUsername(email);
        Assertions.assertNotNull(userDetails);
        Assertions.assertTrue(userDetails.getUsername().equals("Test"));
        Assertions.assertTrue(userDetails.getPassword().equals("pass"));
        Assertions.assertTrue(userDetails.getEmail().equals(email));
    }

    @Test
    void testLoadUserByUsernameThrowsUsernameNotFoundException() {
        String email = "test@test.ru";
        User storedUser = null;
        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(Optional.ofNullable(storedUser));
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
    }

    @Test
    void testAddUser() {
        long expectedId = 5;
        Optional<User> noUser = Optional.ofNullable(null);
        Role userRole = new Role(ROLE_USER);
        User user = new User("test@test.ru", "pass", "Test", true,true,true,true);
        userRole.setUser(user);
        user.addRole(userRole);

        Mockito.when(userRepository.findUserByEmail(user.getEmail())).thenReturn(noUser);
        Mockito.when(userRepository.save(user)).thenReturn(expectedId);

        try {
            long savedUserId = userService.addUser(user);
            Assertions.assertTrue(expectedId == savedUserId);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void testAddUserThrowsUserAlreadyExistException() {
        User user = new User("test@test.ru", "pass", "Test", true,true,true,true);
        Mockito.when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Assertions.assertThrows(UserAlreadyExistException.class, () -> userService.addUser(user));
    }

    @Test
    void testGetUsersList() {
        String email1 = "test1@test.ru";
        String email2 = "test2@test.ru";
        User user1 = new User(email1, "pass1", "Test1", true,true,true,true);
        User user2 = new User(email2, "pass2", "Test2", true,true,true,true);
        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        try {
            List<User> usersList = userService.getUsersList();
            Assertions.assertTrue(usersList.size() == 2);
            Assertions.assertTrue(usersList.get(0).getEmail().equals(email1));
            Assertions.assertTrue(usersList.get(1).getEmail().equals(email2));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetUsersListThrowsNotFoundException() {
        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList());
        Assertions.assertThrows(NotFoundException.class, () -> userService.getUsersList());
    }

    @Test
    void testGetUserById() {
        long searchedUserId = 7;
        User storedUser = new User("test@test.ru", "pass", "Test", true,true,true,true);
        storedUser.setId(searchedUserId);
        Mockito.when(userRepository.findById(searchedUserId)).thenReturn(storedUser);

        try {
            User resultUser = userService.getUserById(searchedUserId);
            Assertions.assertTrue(searchedUserId == resultUser.getId());
            Assertions.assertTrue(resultUser.getEmail().equals(storedUser.getEmail()));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetUserByIdThrowsNotFoundException() {
        long searchedUserId = 7;
        Mockito.when(userRepository.findById(searchedUserId)).thenReturn(null);
        Assertions.assertThrows(NotFoundException.class, ()->userService.getUserById(searchedUserId));
    }

    @Test
    void testDeleteUser() {
        long searchedUserId = 7;
        User storedUser = new User("test@test.ru", "pass", "Test", true,true,true,true);
        try {
            Mockito.when(userRepository.findById(searchedUserId)).thenReturn(storedUser);
            userService.deleteUser(searchedUserId);
            Mockito.verify(userRepository).delete(storedUser);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void testDeleteUserThrowsNotFoundException() {
        long searchedUserId = 7;
        Mockito.when(userRepository.findById(searchedUserId)).thenReturn(null);
        Assertions.assertThrows(NotFoundException.class, () -> userService.getUserById(searchedUserId));
    }

    @Test
    void testUpdateUser() {
        long userId = 1;
        User storedUser = new User("stored@test.ru", "pass", "Stored User", true,true,true,true);
        User updatedUser = new User("updated@test.ru", "pass", "Updated User", true,true,true,true);
        storedUser.setId(userId);
        updatedUser.setId(userId);

        try {
            Mockito.when(userRepository.findById(updatedUser.getId())).thenReturn(storedUser);
            userService.updateUser(updatedUser, userId);
            Mockito.verify(userRepository).update(updatedUser, storedUser.getId());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void testUpdateUserThrowsNotFoundException() {
        long userId = 1;
        User updatedUser = new User("updated@test.ru", "pass", "Updated User", true,true,true,true);
        updatedUser.setId(userId);
        Mockito.when(userRepository.findById(userId)).thenReturn(null);
        Assertions.assertThrows(NotFoundException.class, () -> userService.updateUser(updatedUser, userId));
    }

}
