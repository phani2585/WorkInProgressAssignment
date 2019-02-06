package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
    public class UserAdminBusinessService {

        @Autowired
        private UserDao userDao;

        @Autowired
        private QuestionDao questionDao;

        @Autowired
        private PasswordCryptographyProvider cryptographyProvider;

        public UserEntity getUser(final String userUuid,final String authorizationToken) throws
                UserNotFoundException, AuthorizationFailedException {
            UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
            if(userAuthTokenEntity == null){
                throw new AuthorizationFailedException("ATHR-001","User has not signed in");
            } else if(userAuthTokenEntity.getLogoutAt()!=null){
                throw new AuthorizationFailedException("ATHR-002","User is signed out");
            } else if (userDao.getUserByUuid(userUuid).getRole().equalsIgnoreCase("NONADMIN")) {
                throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
            } else if (userDao.getUserByUuid(userUuid)== null) {
                throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
            } else {
                return userDao.getUserByUuid(userUuid);
            }

        }

    public UserEntity updateUserEntity(final UserEntity userEntity) {
        if (userEntity.getRole().equalsIgnoreCase("ADMIN")) {
            String password = userEntity.getPassword();
            if (password == null) {
                userEntity.setPassword("quora@123");
            }
            String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
            userEntity.setSalt(encryptedText[0]);
            userEntity.setPassword(encryptedText[1]);

        }
        return userDao.setUpdatedUserEntity(userEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity updateQuestion(final QuestionEntity questionEntity, final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {

            UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
            if (userAuthTokenEntity == null) {
                throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
            } else if (userAuthTokenEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
            }

        if (questionEntity.getUser()==userAuthTokenEntity.getUser()) {

            QuestionEntity existingQuestionEntity = questionDao.getQuestionByQUuid(questionEntity.getUuid());

            if (existingQuestionEntity == null) {
                throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
            }
            questionEntity.setUuid(existingQuestionEntity.getUuid());
            questionEntity.setDate(existingQuestionEntity.getDate());
            questionEntity.setUser(existingQuestionEntity.getUser());
            return questionDao.updateQuestion(questionEntity);
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
    }


    }
