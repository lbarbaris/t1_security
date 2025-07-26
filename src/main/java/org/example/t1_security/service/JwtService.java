package org.example.t1_security.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.t1_security.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = new SecretKeySpec(secret.getBytes(), "AES");
    }

    public String generateAccessToken(User user) {
        return generateToken(user, jwtExpiration);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, refreshExpiration);
    }

    private String generateToken(User user, long expirationTimeMillis) {
        try {
            var claimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .claim("roles", user.getRoles().stream().map(r -> r.getName().name()).toList())
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + expirationTimeMillis))
                    .build();

            var header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A256GCM);
            var jwt = new EncryptedJWT(header, claimsSet);

            var encrypter = new DirectEncrypter(secretKey);
            jwt.encrypt(encrypter);

            log.info("Generated JWT: {}", jwt.serialize());

            return jwt.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, JWTClaimsSet::getSubject);
    }

    public boolean isTokenValid(String token, User user) {
        try {
            var username = extractUsername(token);
            return username.equals(user.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, JWTClaimsSet::getExpirationTime).before(new Date());
    }

    private <T> T extractClaim(String token, Function<JWTClaimsSet, T> resolver) {
        try {
            var jwt = EncryptedJWT.parse(token);
            jwt.decrypt(new DirectDecrypter(secretKey));
            var claims = jwt.getJWTClaimsSet();
            return resolver.apply(claims);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract claim", e);
        }
    }
}
