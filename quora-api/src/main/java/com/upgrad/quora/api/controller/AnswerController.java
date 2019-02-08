package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.CreateAnswerBusinessService;
import com.upgrad.quora.service.business.DeleteAnswerBusinessService;
import com.upgrad.quora.service.business.GetAllAnswersToQuestionBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
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

    @Autowired
    private DeleteAnswerBusinessService deleteAnswerBusinessService;

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

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest answerEditRequest , @PathVariable("answerId") final String answerId, @RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {
        String[] bearerToken = accessToken.split("Bearer ");
        AnswerEntity answerEntity = editQuestionContentBusinessService.verifyUserStatus(questionId,bearerToken[1]);
        questionEntity.setContent(questionEditRequest.getContent());
        QuestionEntity updatedQuestionEntity = editQuestionContentBusinessService.updateQuestion(questionEntity);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(updatedQuestionEntity.getUuid()).status("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

    @RequestMapping(method= RequestMethod.DELETE,path="/answer/delete/{answerId}",produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") final String answerUuid,
                                                               @RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {

        String [] bearerToken = accessToken.split("Bearer ");
        final AnswerEntity answerEntityToDelete=deleteAnswerBusinessService.verifyAnsUuid(answerUuid);
        final UserEntity signedinUserEntity = deleteAnswerBusinessService.verifyAuthToken(bearerToken[1]);
        final String Uuid = deleteAnswerBusinessService.deleteAnswer(answerEntityToDelete,signedinUserEntity);
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse()
                .id(Uuid)
                .status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse,HttpStatus.OK);
    }




}
