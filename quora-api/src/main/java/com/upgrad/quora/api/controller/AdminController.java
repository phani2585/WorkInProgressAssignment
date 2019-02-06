package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.business.UserDeleteBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AdminController {
    @Autowired
    UserDeleteBusinessService userDeleteBusinessService;

    @Autowired
    UserAdminBusinessService userAdminBusinessService;

    @RequestMapping(method= RequestMethod.DELETE,path="/admin/user/{userId}",produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> deleteUser(@PathVariable("userId") final String userUuid,
                                                         @RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException, UserNotFoundException {
        String [] bearerToken = accessToken.split("Bearer ");
        UserEntity userEntity=userAdminBusinessService.getUser(userUuid, bearerToken[1]);
        UserEntity userEntityToDelete=userAdminBusinessService.updateUserEntity(userEntity);
        final String Uuid = userDeleteBusinessService.deleteUser(userEntityToDelete);
        UserDeleteResponse userDeleteResponse = new UserDeleteResponse()
                .id(Uuid)
                .status("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);
    }

}
