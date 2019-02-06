package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerDetailsResponse;
import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.CreateAnswerBusinessService;
import com.upgrad.quora.service.business.GetAllAnswersToQuestionBusinessService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private CreateAnswerBusinessService createAnswerBusinessService;

    @Autowired
    private GetAllAnswersToQuestionBusinessService getAllAnswersToQuestionBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@PathVariable("questionId") final String questionId, @RequestHeader("accessToken") final String accessToken, final AnswerRequest answerRequest) throws AuthorizationFailedException, InvalidQuestionException {

        String [] bearerToken = accessToken.split("Bearer ");
        UserAuthTokenEntity userAuthTokenEntity=createAnswerBusinessService.verifyAuthToken(bearerToken[1]);
        AnswerEntity answerEntity=new AnswerEntity();
        answerEntity.setQuestionEntity(createAnswerBusinessService.verifyQuestionId(questionId));
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setUser(userAuthTokenEntity.getUser());
        answerEntity.setAns(answerRequest.getAnswer());
        final ZonedDateTime now = ZonedDateTime.now();
        answerEntity.setDate(now);
        final AnswerEntity createdAnswerEntity = createAnswerBusinessService.createAnswer(answerEntity);
        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable("questionId")final String questionId, @RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        String[] bearerToken = accessToken.split("Bearer ");
        UserAuthTokenEntity userAuthTokenEntity = getAllAnswersToQuestionBusinessService.verifyAuthToken(bearerToken[1]);
        List<AnswerEntity> allAnswersByQuestion = new ArrayList<AnswerEntity>();
        allAnswersByQuestion.addAll(getAllAnswersToQuestionBusinessService.getAllAnswersByQuestion(getAllAnswersToQuestionBusinessService.verifyQuestionId(questionId)));
        List<AnswerDetailsResponse> answerDetailsResponseList = new ArrayList<AnswerDetailsResponse>();

        for (AnswerEntity answer : allAnswersByQuestion) {
            AnswerDetailsResponse answerDetailsResponse=new AnswerDetailsResponse();
            answerDetailsResponseList.add(answerDetailsResponse.id(answer.getUuid()).answerContent(answer.getAns()).questionContent(answer.getQuestionEntity().getContent()));
        }

        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponseList,HttpStatus.OK);

    }




}
