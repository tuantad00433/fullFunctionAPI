package com.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;


@Entity
public class Credential {
    @Id
    String userId;
    @Index
    String tokenKey;
    @Index
    long createdTime;

    public Credential() {
        createdTime = System.currentTimeMillis();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
}
