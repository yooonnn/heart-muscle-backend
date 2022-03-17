package shop.heartmuscle.heartmuscle.service;

import org.springframework.data.domain.Page;
import shop.heartmuscle.heartmuscle.domain.Comment;
import shop.heartmuscle.heartmuscle.domain.Feed;
import shop.heartmuscle.heartmuscle.dto.CommentRequestDto;
import shop.heartmuscle.heartmuscle.dto.FeedRequestDto;
import shop.heartmuscle.heartmuscle.security.UserDetailsImpl;

import java.io.IOException;

public interface FeedService {

    Feed saveFeed(FeedRequestDto feedRequestDto, UserDetailsImpl nowUser) throws IOException;

    Page<Feed> getFeeds(int page, int size, String sortBy, boolean isAsc);

    String checkUser(Long id, UserDetailsImpl nowUser);

    Feed getFeed(Long id);

    Long update(Long id, FeedRequestDto feedRequestDto);

    Long delete(Long id);

    Comment saveComment(CommentRequestDto commentRequestDto, UserDetailsImpl nowUser) throws IOException;

}
