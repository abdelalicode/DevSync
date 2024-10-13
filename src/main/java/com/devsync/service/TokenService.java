package com.devsync.service;


import com.devsync.domain.entity.Tag;
import com.devsync.domain.entity.Token;
import com.devsync.domain.entity.User;
import com.devsync.repository.Implementations.TagRepository;
import com.devsync.repository.Implementations.TokenRepository;

import java.util.List;


public class TokenService {


    private TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = new TokenRepository();
    }


    public List<Token> getTokens() {
        return tokenRepository.findAll();
    }

    public Token getTokenByUser(User user) {
        return tokenRepository.findByUser(user);
    }









}
