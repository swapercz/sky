package com.sky.users.user;

import com.sky.users.user.to.CreateUserTO;
import com.sky.users.user.to.UpdateUserTO;
import com.sky.users.user.to.UserTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private static final Long USER_ID = 1L;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    private User user;
    private CreateUserTO createUserTO;
    private UpdateUserTO updateUserTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(USER_ID);
        user.setName("User Test");
        user.setEmail("email@email.com");
        user.setPassword("pwd");

        createUserTO = new CreateUserTO("email@email.com", "pwd", "User Test");
        updateUserTO = new UpdateUserTO("newPwd", "New Name");

    }

    @Test
    void testFindUserByIdUserExists() {
        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
        Optional<UserTO> selectedUser = userService.findUserById(USER_ID);

        assertTrue(selectedUser.isPresent());
        UserTO user = selectedUser.get();
        assertEquals("User Test", user.name());
        assertEquals("pwd", user.password());
        assertEquals("email@email.com", user.email());

        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void testFindUserByIdUserNotFound() {
        given(userRepository.findById(USER_ID)).willReturn(Optional.empty());
        Optional<UserTO> selectedUser = userService.findUserById(USER_ID);

        assertTrue(selectedUser.isEmpty());

        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void testSaveUserSuccess() {
        given(userRepository.save(any())).willReturn(user);

        UserTO persistedUser = userService.saveUser(createUserTO);

        assertNotNull(persistedUser);
        assertEquals("User Test", persistedUser.name());
        assertEquals("pwd", persistedUser.password());
        assertEquals("email@email.com", persistedUser.email());

        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testSaveUserMapperError() {
        given(userRepository.save(any())).willThrow(new DataIntegrityViolationException("Already exists"));
        assertThrows(DataIntegrityViolationException.class, () -> userService.saveUser(createUserTO));
    }

    @Test
    void testUpdateUserSuccess() {
        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
        given(userRepository.save(user)).willReturn(user);
        Optional<UserTO> updatedUser = userService.updateUser(USER_ID, updateUserTO);

        assertTrue(updatedUser.isPresent());

        UserTO returnedUser = updatedUser.get();

        assertEquals("New Name", returnedUser.name());
        assertEquals("12a979c08f64c4d08d22ebedd459a8f33eaca9557c0232e65d08f71d35d0dda2", returnedUser.password());
        assertEquals("email@email.com", returnedUser.email());

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserNotFound() {
        given(userRepository.findById(USER_ID)).willReturn(Optional.empty());
        Optional<UserTO> updatedUser = userService.updateUser(USER_ID, updateUserTO);

        assertTrue(updatedUser.isEmpty());

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteUserSuccess() {
        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
        userService.deleteUser(USER_ID);

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, times(1)).delete(user);

    }

    @Test
    void testDeleteUserUserDoesNotExist() {
        given(userRepository.findById(USER_ID)).willReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () -> userService.deleteUser(USER_ID));

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, never()).delete(any());
    }

}

