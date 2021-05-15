package wooteco.member.auth;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.member.controller.dto.response.SignInResponse;
import wooteco.subway.AcceptanceTest;

import java.util.HashMap;
import java.util.Map;

import static wooteco.member.MemberAcceptanceTest.회원_생성을_요청;
import static wooteco.member.MemberAcceptanceTest.회원_정보_조회됨;

public class AuthAcceptanceTest extends AcceptanceTest {
    private static final String EMAIL = "email@email.com";
    private static final String PASSWORD = "password";
    private static final Integer AGE = 20;

    @DisplayName("Bearer Auth")
    @Test
    void myInfoWithBearerAuth() {
        // given
        회원_등록되어_있음(EMAIL, PASSWORD, AGE);
        SignInResponse signInResponse = 로그인되어_있음(EMAIL, PASSWORD);

        // when
        ExtractableResponse<Response> response = 내_회원_정보_조회_요청(signInResponse);

        // then
        회원_정보_조회됨(response, EMAIL, AGE);
    }

    @DisplayName("Bearer Auth 로그인 실패")
    @Test
    void myInfoWithBadBearerAuth() {
        회원_등록되어_있음(EMAIL, PASSWORD, AGE);

        Map<String, String> params = new HashMap<>();
        params.put("email", EMAIL + "OTHER");
        params.put("password", PASSWORD);

        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/api/sign-in")
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("Bearer Auth 유효하지 않은 토큰")
    @Test
    void myInfoWithWrongBearerAuth() {
        SignInResponse signInResponse = new SignInResponse("accesstoken");

        RestAssured
                .given().log().all()
                .auth().oauth2(signInResponse.getAccessToken())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/members/me")
                .then().log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    public static ExtractableResponse<Response> 회원_등록되어_있음(String email, String password, Integer age) {
        return 회원_생성을_요청(email, password, age);
    }

    public static SignInResponse 로그인되어_있음(String email, String password) {
        ExtractableResponse<Response> response = 로그인_요청(email, password);
        return response.as(SignInResponse.class);
    }

    public static ExtractableResponse<Response> 로그인_요청(String email, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        return RestAssured.given().log().all().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(params).
                when().
                post("/api/sign-in").
                then().
                log().all().
                statusCode(HttpStatus.OK.value()).
                extract();
    }

    public static ExtractableResponse<Response> 내_회원_정보_조회_요청(SignInResponse signInResponse) {
        return RestAssured.given().log().all().
                auth().oauth2(signInResponse.getAccessToken()).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                get("/api/members/me").
                then().
                log().all().
                statusCode(HttpStatus.OK.value()).
                extract();
    }
}
