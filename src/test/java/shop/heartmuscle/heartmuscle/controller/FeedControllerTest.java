package shop.heartmuscle.heartmuscle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import shop.heartmuscle.heartmuscle.domain.Feed;
import shop.heartmuscle.heartmuscle.domain.User;
import shop.heartmuscle.heartmuscle.domain.UserRole;
import shop.heartmuscle.heartmuscle.dto.FeedRequestDto;
import shop.heartmuscle.heartmuscle.repository.FeedRepository;
import shop.heartmuscle.heartmuscle.repository.UserRepository;
import shop.heartmuscle.heartmuscle.security.UserDetailsImpl;

import javax.transaction.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class FeedControllerTest {

    @Autowired
    // 서버 구동없이 임의의 객체를 만들어 mvc 동작
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    UserRepository userRepository;

    private Long feedId;
    private User testUser;
    private UserDetailsImpl nowUser;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }


    @BeforeTransaction
    void registerUser() {
        testUser = new User("testUsername", "testPassword", "test@email.com", UserRole.USER, "testNickname", "https://teamco-bucket.s3.ap-northeast-2.amazonaws.com/Profile-PNG-Clipart.png");
        userRepository.save(testUser);
    }

//    @BeforeTransaction
//    void loginUser() {
//    }

    @AfterTransaction
    void deleteAll() {
//        Long id = testUser.getId();
//        userRepository.deleteById(id);
        userRepository.deleteAll();
        feedRepository.deleteAll();
    }
//    @AfterEach
//    void tearDown() {
//    }

    @Test
    @DisplayName("피드 작성")
    @WithUserDetails(value = "testUsername")
    @Order(1)
    void createFeed() throws Exception {

        MockMultipartFile image = new MockMultipartFile("image", "image.jpeg", "image/jpeg", "<<jpeg data>>".getBytes());
        FeedRequestDto feedRequestDto = new FeedRequestDto("testTitle", "testContent", "testUsername", "testTag", image);
        Feed feed = new Feed(feedRequestDto, "https://teamco-spring-project.s3.ap-northeast-2.amazonaws.com/logo.png", testUser);
//        feedRepository.save(feed);

        mockMvc.perform(multipart("/feed")
                        .file(image)
                        .param("title", feedRequestDto.getTitle())
                        .param("content", feedRequestDto.getContent())
                        .param("tags", feedRequestDto.getTags())
                        .param("username", feedRequestDto.getUsername())
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("피드 조회")
    @WithUserDetails(value = "testUsername")
    @Order(2)
    void getFeed() throws Exception {
        mockMvc.perform(get("/feed")
                        .param("page", "1")
                        .param("size", "2")
                        .param("sortBy", "createdAt")
                        .param("isAsc", "true"))
                .andExpect(status().isOk())
                .andDo(print());
    }

//    @Test
//    @DisplayName("피드 작성자 확인")
//    @WithUserDetails(value = "testUsername")
//    @Order(3)
//    void checkUser() throws Exception {
//        mockMvc.perform(get("/feed/check/{id}", feedId)
//                        .param("nowUser", "testUsername"))
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("피드 수정")
//    @WithUserDetails(value = "testUsername")
//    @Order(3)
//    void updateFeed() throws Exception {
//
//        MockMultipartFile image = new MockMultipartFile("image", "image.jpeg", "image/jpeg", "<<jpeg data>>".getBytes());
//        FeedRequestDto feedRequestDto = new FeedRequestDto("testTitle", "testContent", "testUsername", "testTag", image);
//        Feed feed = new Feed(feedRequestDto, "https://teamco-spring-project.s3.ap-northeast-2.amazonaws.com/logo.png", testUser);
//        feedRepository.save(feed);
//
//        Long id = feed.getId();
//
//        FeedRequestDto newFeed = new FeedRequestDto("newTestTitle", "newTestContent", "newTestUsername", "newTestTag", image);
//
//        String jsonString = objectMapper.writeValueAsString(newFeed);
//        MockMultipartFile jsonPart = new MockMultipartFile("newFeed", "json", "application/json", jsonString.getBytes(StandardCharsets.UTF_8));
//
//        mockMvc.perform(multipart("/feed/{id}", id)
//                        .part(new MockPart("title", "newTestTitle".getBytes(StandardCharsets.UTF_8)))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(String.valueOf(jsonPart))
//
////                        .with(new RequestPostProcessor() {
////                            @Override
////                            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
////                                request.setMethod("PUT");
////                                return request;
////                            }
////                        }
////                        )
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("title", is("newTestTitle")))
//                .andExpect(jsonPath("content", is("newTestContent")))
//                .andDo(print());
//
//
//    }

    @Test
    @DisplayName("피드 수정 2")
    @WithUserDetails(value = "testUsername")
    @Order(3)
    void updateFeed2() throws Exception {

        ObjectNode feed = objectMapper.createObjectNode();
        feed.put("title", "newTestTitle");
        feed.put("content", "newTestContent");
//        feed.put("imageUrl", String.valueOf(image));

        Long id = 1L;

        mockMvc.perform(put("/feed/{id}", id)
                .content(objectMapper.writeValueAsString(feed))
                .contentType(MediaType.APPLICATION_JSON)
        );

    }


    @Test
    void deleteFeed() {
    }

    @Test
    void createComment() {
    }
}