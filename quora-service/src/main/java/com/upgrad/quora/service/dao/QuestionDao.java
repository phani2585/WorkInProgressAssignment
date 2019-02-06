package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public QuestionEntity getQuestionByQUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("questionByQUuid", QuestionEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {

            return null;
        }
    }

    public List<QuestionEntity> getQuestionsByUserId(final UserEntity user){
        try {
            return entityManager.createNamedQuery("QuestionByUserId", QuestionEntity.class).setParameter("user", user).getResultList();
        } catch (NoResultException nre) {

            return null;
        }
    }
}
