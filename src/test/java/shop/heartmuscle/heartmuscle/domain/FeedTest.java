package shop.heartmuscle.heartmuscle.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FeedTest {

    private User user;

    @BeforeEach
    void login() {
        user = new User("testUsername", "testPassword", "test@email.com", UserRole.USER, "testNickname", "https://teamco-bucket.s3.ap-northeast-2.amazonaws.com/Profile-PNG-Clipart.png");
    }

    @Test
    @DisplayName("피드 작성하기")
    @Order(1)
    void createFeed_Normal(){
        String title = "테스트 제목";
        String content = "테스트 내용";
        String tags = "123";
        String username = "유저아이디";
        String imageUrl = "";
    }

}