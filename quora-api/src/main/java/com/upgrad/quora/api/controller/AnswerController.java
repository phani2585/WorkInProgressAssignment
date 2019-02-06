package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.CreateAnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private CreateAnswerBusinessService createAnswerBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@PathVariable("questionId") final String questionId, @RequestHeader("accessToken") final String accessToken, final AnswerRequest answerRequest) throws AuthorizationFailedException, InvalidQuestionException {

        String [] bearerToken = accessToken.split("Bearer ");
        UserAuthTokenEntity userAuthTokenEntity=createAnswerBusinessService.verifyAuthToken(bearerToken[1]);
        AnswerEntity answerEntity=new AnswerEntity();
        answerEntity.setQuestionEntity(createAnswerBusinessService.verifyQuestionId(questionId));
        answerEntity.setUuid(userAuthTokenEntity.getUuid());
        answerEntity.setUser(userAuthTokenEntity.getUser());
        answerEntity.setAns(answerRequest.getAnswer());
        final ZonedDateTime now = ZonedDateTime.now();
        answerEntity.setDate(now);
        final AnswerEntity createdAnswerEntity = createAnswerBusinessService.createAnswer(answerEntity);
        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }




}
