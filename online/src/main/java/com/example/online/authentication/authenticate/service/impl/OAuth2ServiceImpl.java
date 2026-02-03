package com.example.online.authentication.authenticate.service.impl;

import com.example.online.domain.model.OnetimeToken;
import com.example.online.enumerate.OneTimeTokenType;
import com.example.online.helper.Base64Generator;
import com.example.online.authentication.authenticate.service.OAuth2Service;
import com.example.online.domain.model.User;
import com.example.online.helper.Sha256Hashing;
import com.example.online.repository.OnetimeTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OAuth2ServiceImpl implements OAuth2Service {
    private final OnetimeTokenRepository onetimeTokenRepository;

    public String generateAuthorizationCode(User user){
        String rawCode = Base64Generator.generateCode();
        String rawHashed = Sha256Hashing.sha256(rawCode);

        onetimeTokenRepository.save(
                OnetimeToken.builder()
                        .token(rawHashed)
                        .type(OneTimeTokenType.AUTHORIZATION_CODE)
                        .expiredAt(Instant.now().plusSeconds(8))
                        .build()
        );
        return rawCode;
    }
}
