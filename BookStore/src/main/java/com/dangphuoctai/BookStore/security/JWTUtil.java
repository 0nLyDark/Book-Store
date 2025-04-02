package com.dangphuoctai.BookStore.security;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.dangphuoctai.BookStore.config.UserInfoConfig;
import com.dangphuoctai.BookStore.payloads.dto.OtpDTO;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Component
public class JWTUtil {

    @Value("${jwt_secret}")
    private String jwt_key;

    public String generateToken(UserDTO userDTO) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject("User")
                .issuer("Auth")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .claim("email", userDTO.getEmail())
                .claim("userId", userDTO.getUserId())
                .claim("scope", buildScope(userDTO))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(jwt_key.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildScope(UserDTO userDTO) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(userDTO.getRoles())) {
            userDTO.getRoles().forEach(s -> stringJoiner.add(s.getRoleName()));
        }
        return stringJoiner.toString();

    }

    public boolean validateToken(String token) throws JOSEException, ParseException {

        JWSVerifier verifier = new MACVerifier(jwt_key.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryDate = signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);

        return verified && expiryDate.after(new Date());
    }

    public UserInfoConfig extractToken(String token) throws ParseException, JOSEException {
        UserInfoConfig userInfoConfig = new UserInfoConfig();
        JWSObject jwsObject = JWSObject.parse(token);
        Map<String, Object> object = jwsObject.getPayload().toJSONObject();
        userInfoConfig.setUserId((Long) object.get("userId"));
        userInfoConfig.setEmail((String) object.get("email"));
        userInfoConfig.setRole((String) object.get("scope"));

        return userInfoConfig;
    }

    public String generateCodeOTP(String email, String otp) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject("User")
                .issuer("Auth")
                .claim("email", email)
                .claim("otp", otp)
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(jwt_key.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public OtpDTO extractCodeOTP(String code) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(code);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            OtpDTO otpDTO = new OtpDTO();
            otpDTO.setEmail(claims.getClaimAsString("email"));
            otpDTO.setCode(claims.getClaimAsString("otp"));
            return otpDTO;
        } catch (ParseException e) {
            throw new RuntimeException("Error while verifying JWT", e);
        }
    }
}
