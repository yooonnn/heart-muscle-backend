package shop.heartmuscle.heartmuscle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import shop.heartmuscle.heartmuscle.domain.Comment;
import shop.heartmuscle.heartmuscle.domain.Feed;
import shop.heartmuscle.heartmuscle.domain.User;
import shop.heartmuscle.heartmuscle.domain.WorkoutTag;
import shop.heartmuscle.heartmuscle.dto.CommentRequestDto;
import shop.heartmuscle.heartmuscle.dto.FeedRequestDto;
import shop.heartmuscle.heartmuscle.repository.CommentRepository;
import shop.heartmuscle.heartmuscle.repository.FeedRepository;
import shop.heartmuscle.heartmuscle.repository.UserRepository;
import shop.heartmuscle.heartmuscle.repository.WorkoutTagRepository;
import shop.heartmuscle.heartmuscle.security.UserDetailsImpl;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FeedServiceImpl implements FeedService{

    private final FeedRepository feedRepository;
    private final CommentRepository commentRepository;
    private final AwsService awsService;
    private final WorkoutTagRepository workoutTagRepository;
    private final UserRepository userRepository;

    // 피드 작성
    @Override
    @Transactional
    public Feed saveFeed(FeedRequestDto feedRequestDto, UserDetailsImpl nowUser) throws IOException {
        String url = null;

        if(feedRequestDto.getImage() != null) url = awsService.upload(feedRequestDto.getImage());
        else url = "https://teamco-spring-project.s3.ap-northeast-2.amazonaws.com/logo.png";
        User user = userRepository.findById(nowUser.getId()).orElseThrow(
                () -> new NullPointerException("사용자 정보가 존재하지 않습니다.")
        );

        Feed feed = new Feed(feedRequestDto, url, user);
        feedRepository.save(feed);

        List<String> items = Arrays.asList(feedRequestDto.getTags().split("\\s*,\\s*"));
        List<WorkoutTag> tags = items.stream().map(tag -> new WorkoutTag(tag, feed)).collect(Collectors.toList());
        workoutTagRepository.saveAll(tags);

        return feed;
    }

    // 피드 전체 목록 + 페이징 처리
    @Override
    public Page<Feed> getFeeds(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return feedRepository.findAll(pageable);
    }

    // 피드 상세 조회
    @Override
    public Feed getFeed(Long id) {
        return feedRepository.findById(id).orElseThrow(
                () -> new NullPointerException("게시글이 존재하지 않습니다.")
        );
    }

    // 현재 로그인 사용자 - 피드 작성자 일치 여부 확인
    @Override
    public String checkUser(Long id, UserDetailsImpl nowUser) {

        String check = null;

        // 토큰값으로 현재 로그인중인 아이디 조회 (nowuser)
        Long nowuser = nowUser.getId();

        // 피드 작성자 아이디 조회 (feeduser)
        Feed checkfeed = feedRepository.findById(id).orElseThrow(
                () -> new NullPointerException("게시글이 존재하지 않습니다.")
        );

        User findfeeduser = checkfeed.getUser(); // 해당 User
        Long feeduser = findfeeduser.getId(); // user - id

        if (nowuser == feeduser) check = "true";
        else check = "false";

        return check;
    }

    // 피드 수정
    @Override
    @Transactional
    public Long update(Long id, FeedRequestDto feedRequestDto) {
        Feed feed = feedRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
        );
        feed.update(feedRequestDto);
        return feed.getId();
    }

    // 피드 삭제
    @Override
    public Long delete(Long id) {
        feedRepository.deleteById(id);
        return id;
    }

    // 피드 댓글 작성
    @Override
    @Transactional
    public Comment saveComment(CommentRequestDto commentRequestDto, UserDetailsImpl nowUser) throws IOException {
        Feed feed = feedRepository.findById(commentRequestDto.getId()).orElseThrow(
                () -> new NullPointerException("게시글이 존재하지 않습니다.")
        );

        User user = userRepository.findById(nowUser.getId()).orElseThrow(
                () -> new NullPointerException("사용자 정보가 존재하지 않습니다.")
        );

        String commentUser = user.getUsername();

        Comment comment = new Comment(commentRequestDto, feed, commentUser);
        commentRepository.save(comment);
        return comment;
    }
}
