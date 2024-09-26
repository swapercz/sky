package com.sky.users.user;

import com.sky.users.user.to.CreateUserTO;
import com.sky.users.user.to.UpdateUserTO;
import com.sky.users.user.to.UserTO;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.sky.users.config.CacheConfig.USER_CACHE;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserDoesNotExistException("User not found"));
    }

    @Cacheable(cacheNames = USER_CACHE, key = "#a0", unless = "#result == null")
    public Optional<UserTO> findUserById(Long userId) {
        return userRepository.findById(userId).flatMap(user -> Optional.of(UserMapper.INSTANCE.toUserTO(user)));
    }

    @Observed(name = "user.create", contextualName = "saveUser")
    public UserTO saveUser(CreateUserTO createUserTO) {
        var persistedUser = userRepository.save(UserMapper.INSTANCE.toUser(createUserTO));
        logger.info("User '{}' have been created.", persistedUser.getName());
        return UserMapper.INSTANCE.toUserTO(persistedUser);
    }

    @CacheEvict(cacheNames = USER_CACHE, key = "#a0")
    public Optional<UserTO> updateUser(Long userId, UpdateUserTO user) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    existingUser.setName(user.name());
                    existingUser.setPassword(UserMapper.INSTANCE.toSha3(user.password()));
                    var updatedUser = userRepository.save(existingUser);
                    return Optional.of(UserMapper.INSTANCE.toUserTO(updatedUser));
                })
                .orElse(Optional.empty());
    }

    @CacheEvict(cacheNames = USER_CACHE, key = "#a0")
    public void deleteUser(Long userId) {
        var userToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("User with id '%d' not exist".formatted(userId)));
        userRepository.delete(userToRemove);
        logger.info("User with name '{}' has been deleted.", userToRemove.getName());
    }


}
