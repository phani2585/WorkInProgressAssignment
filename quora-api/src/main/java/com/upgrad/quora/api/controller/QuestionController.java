package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.CreateQuestionBusinessService;
import com.upgrad.quora.service.business.GetAllQuestionsBusinessService;
import com.upgrad.quora.service.business.GetAllQuestionsByUserBusinessService;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private CreateQuestionBusinessService createQuestionBusinessService;

    @Autowired
    private GetAllQuestionsBusinessService getAllQuestionsBusinessService;

    @Autowired
    private GetAllQuestionsByUserBusinessService getAllQuestionsByUserBusinessService;

    @Autowired
    UserAdminBusinessService userAdminBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("accessToken") final String accessToken, final QuestionRequest questionRequest) throws AuthorizationFailedException, SignOutRestrictedException {

        String[] bearerToken = accessToken.split("Bearer ");
        UserAuthTokenEntity userAuthTokenEntity = createQuestionBusinessService.verifyAuthToken(bearerToken[1]);
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setUser(userAuthTokenEntity.getUser());
        questionEntity.setContent(questionRequest.getContent());
        final ZonedDateTime now = ZonedDateTime.now();
        questionEntity.setDate(now);
        final QuestionEntity createdQuestionEntity = createQuestionBusinessService.createQuestion(questionEntity);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException {
        String[] bearerToken = accessToken.split("Bearer ");
        UserAuthTokenEntity userAuthTokenEntity = getAllQuestionsBusinessService.verifyAuthToken(bearerToken[1]);
        List<QuestionEntity> allQuestions = new ArrayList<QuestionEntity>();
        allQuestions.addAll(getAllQuestionsBusinessService.getAllQuestions());
        List<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<QuestionDetailsResponse>();

        for (QuestionEntity question : allQuestions) {
            QuestionDetailsResponse questionDetailsResponse=new QuestionDetailsResponse();
            questionDetailsResponses.add(questionDetailsResponse.id(question.getUuid()).content(question.getContent()));
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses,HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.GET, path = "question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable("userId")final String userId,@RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException, UserNotFoundException {
        String[] bearerToken = accessToken.split("Bearer ");
        UserAuthTokenEntity userAuthTokenEntity = getAllQuestionsByUserBusinessService.verifyAuthToken(userId,bearerToken[1]);
        List<QuestionEntity> allQuestionsByUser = new ArrayList<QuestionEntity>();
        allQuestionsByUser.addAll(getAllQuestionsByUserBusinessService.getAllQuestionsByUserId((userAuthTokenEntity.getUser())));
        List<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<QuestionDetailsResponse>();

        for (QuestionEntity question : allQuestionsByUser) {
            QuestionDetailsResponse questionDetailsResponse=new QuestionDetailsResponse();
             questionDetailsResponses.add(questionDetailsResponse.id(question.getUuid()).content(question.getContent()));
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses,HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(final QuestionEditRequest questionEditRequest , @PathVariable("questionId") final String questionId, @RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        String[] bearerToken = accessToken.split("Bearer ");
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionEditRequest.getContent());
        QuestionEntity updatedQuestionEntity = userAdminBusinessService.updateQuestion(questionEntity,bearerToken[1]);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(updatedQuestionEntity.getUuid()).status("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }
}

