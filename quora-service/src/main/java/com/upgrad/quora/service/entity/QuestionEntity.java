package com.upgrad.quora.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;


    @Entity
    @Table(name = "question")
    @NamedQueries({
            @NamedQuery(name = "questionByQUuid", query = "select q from QuestionEntity q where q.uuid =:uuid"),
            @NamedQuery(name= "QuestionByUserId",query = "select q from QuestionEntity q where q.user = :user")


    })
    public class QuestionEntity implements Serializable {

        @Id
        @Column(name = "ID")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Column(name = "UUID")
        @Size(max = 200)
        private String uuid;

        @ManyToOne
        @OnDelete(action = OnDeleteAction.CASCADE)
        @JoinColumn(name = "USER_ID")
        private UserEntity user;

        @Column(name = "CONTENT")
        @NotNull
        @Size(max = 500)
        private String content;

        @Column(name = "DATE")
        @NotNull
        private ZonedDateTime date;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public UserEntity getUser() {
            return user;
        }

        public void setUser(UserEntity user) {
            this.user = user;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public ZonedDateTime getDate() {
            return date;
        }

        public void setDate(ZonedDateTime date) {
            this.date = date;
        }
        @Override
        public boolean equals(Object obj) {
            return new EqualsBuilder().append(this, obj).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(this).hashCode();
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }
