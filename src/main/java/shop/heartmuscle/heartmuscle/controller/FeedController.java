package shop.heartmuscle.heartmuscle.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.heartmuscle.heartmuscle.domain.Feed;
import shop.heartmuscle.heartmuscle.dto.*;
import shop.heartmuscle.heartmuscle.security.UserDetailsImpl;
import shop.heartmuscle.heartmuscle.service.FeedService;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class FeedController {

    private final FeedService feedService;

    // 피드 작성
    @Operation(description = "피드 저장하기", method = "POST")
    @PostMapping("/feed")
    public void ImageFeed(FeedRequestDto feedRequestDto, @AuthenticationPrincipal UserDetailsImpl nowUser) throws IOException {
        feedService.saveFeed(feedRequestDto, nowUser);
    }

    // 피드 목록 + 페이징
    @Operation(description = "피드 목록 불러오기", method = "GET")
    @GetMapping("/feed")
    public Page<Feed> getFeeds(@RequestParam("page") int page,
                                       @RequestParam("size") int size,
                                       @RequestParam("sortBy") String sortBy,
                                       @RequestParam("isAsc") boolean isAsc) {
        page = page - 1;
        return feedService.getFeeds(page, size, sortBy, isAsc);
    }

    // 현재 로그인 사용자 - 피드 작성자 일치 여부 확인
    @GetMapping("/feed/check/{id}")
    public String checkUser(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetailsImpl nowUser) {
        return feedService.checkUser(id, nowUser);
    }

    // 피드 상세
    @Operation(description = "피드 상세보기", method = "GET")
    @GetMapping("/feed/{id}")
    public Feed getFeed(@PathVariable Long id) {
        return feedService.getFeed(id);
    }

    // 피드 수정
    @Operation(description = "피드 수정하기", method = "PUT")
    @PutMapping("/feed/{id}")
    public Long updateFeed(@PathVariable Long id, @RequestBody FeedRequestDto feedRequestDto) {
        feedService.update(id, feedRequestDto);
        return id;
    }

    // 피드 삭제
    @Operation(description = "피드 삭제하기", method = "DELETE")
    @DeleteMapping("/feed/{id}")
    public Long deleteFeed(@PathVariable Long id) {
        feedService.delete(id);
        return id;
    }

    // 댓글 작성
    @Operation(description = "피드에 댓글 작성하기", method = "POST")
    @PostMapping("/feed/comment")
    public void saveComment(@RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal UserDetailsImpl nowUser) throws IOException {
        feedService.saveComment(commentRequestDto, nowUser);
    }
}