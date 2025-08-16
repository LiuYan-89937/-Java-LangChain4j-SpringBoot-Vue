package com.liuyan.personalblog.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

public class JwtUtils {

    // 使用 JJWT 安全生成符合 HMAC-SHA256 要求的密钥
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 12小时的过期时间（毫秒）
    private static final long EXPIRATION = 1000 * 60 * 60 * 12;

    /**
     * 生成JWT令牌
     *
     * @param claims 要嵌入到令牌中的声明信息
     * @return JWT令牌字符串
     */
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析并验证JWT令牌
     *
     * @param token 要解析的JWT令牌
     * @return 包含声明信息的Claims对象
     * @throws JwtException 如果令牌无效或已过期
     */
    public static Claims validateToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
