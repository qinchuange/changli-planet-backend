package com.creamakers.usersystem.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;


import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration_time}")
    private long expirationTime;

    @Value("${jwt.refresh_token_expiration_time}")
    private long refreshTokenExpirationTime;

    // 生成简单的访问JWT
    public String generateToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(algorithm);
    }

    /**
     * 生成包含设备ID和时间戳的访问Token
     * @param username
     * @param deviceID
     * @param timeStamp
     * @return
     */
    public String generateToken(String username, String deviceID, Long timeStamp) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.create()
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                    .withClaim("username", username)
                    .withClaim("deviceID", deviceID)
                    .withClaim("timeStamp", timeStamp)
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成包含较长过期时间的Refresh Token
     * @param username
     * @return
     */
    public String generateRefreshToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                .sign(algorithm);
    }

    /**
     * 生成包含设备ID和时间戳的Refresh Token
     * @param username
     * @param deviceID
     * @param timeStamp
     * @return
     */
    public String generateRefreshToken(String username, String deviceID, Long timeStamp) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.create()
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                    .withClaim("username", username)
                    .withClaim("deviceID", deviceID)
                    .withClaim("timeStamp", timeStamp)
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 验证JWT是否有效（只验证签名和过期时间）
     * @param token
     * @return true 如果JWT有效，false 否则
     */
    public boolean validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            // 检查是否过期
            if (isTokenExpired(decodedJWT)) {
                System.out.println("JWT已过期");
                return false;
            }

            return true;
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            System.out.println("JWT验证失败");
            return false;
        }
    }

    /**
     * 从JWT中提取设备ID
     * @param token
     * @return deviceID 或 null
     */
    public String getDeviceIDFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("deviceID").asString();
        } catch (JWTVerificationException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * 从JWT中提取用户名
     * @param token
     * @return username 或 null
     */
    public String getUserNameFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("username").asString();
        } catch (JWTVerificationException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * 从JWT中提取时间戳
     * @param token
     * @return 时间戳 或 null
     */
    public Long getTimeStampFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("timeStamp").asLong();
        } catch (JWTVerificationException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * 检查JWT是否过期
     * @param decodedJWT
     * @return true 如果已过期，false 否则
     */
    private boolean isTokenExpired(DecodedJWT decodedJWT) {
        return decodedJWT.getExpiresAt().before(new Date());
    }

    /**
     * 解码JWT并返回DecodedJWT对象
     * @param token
     * @return DecodedJWT对象 或 null
     */
    private DecodedJWT decodeToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
