package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
    public class UserAdminBusinessService {

        @Autowired
        private UserDao userDao;

        @Autowired
        private PasswordCryptographyProvider cryptographyProvider;

        public UserEntity getUser(final String userUuid,String authorizationToken) throws
                UserNotFoundException {
            UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
            if (userDao.getUserByUuid(userUuid).getRole().equalsIgnoreCase("NONADMIN")) {
                throw new UserNotFoundException("ATHR-003", "Unauthorized Access, Entered user is not an admin");

            } else {
                return userDao.getUserByUuid(userUuid);
            }

        }

    public UserEntity getUserByRole(final String role,UserEntity userEntity) {
        if (userEntity.getSalt() == null && userEntity.getRole().equalsIgnoreCase("ADMIN")) {
            String password = userEntity.getPassword();

            if (password == null) {
                userEntity.setPassword("quora@123");
            }
            String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
            userEntity.setSalt(encryptedText[0]);
            userEntity.setPassword(encryptedText[1]);

        }
        return userDao.createUser(userEntity);
    }

    }
