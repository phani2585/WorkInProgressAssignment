package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private SignupBusinessService signupBusinessService;

   @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {

        final UserEntity userEntity = new UserEntity();

        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setContactNumber(signupUserRequest.getContactNumber());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setSalt("1234abc");
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setRole("nonadmin");


        final UserEntity createdUserEntity = signupBusinessService.signup(userEntity,signupUserRequest.getUserName(),signupUserRequest.getEmailAddress());
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }

}
