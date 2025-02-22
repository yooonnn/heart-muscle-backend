package shop.heartmuscle.heartmuscle.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.heartmuscle.heartmuscle.dto.FeedRequestDto;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Feed extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = true)
    private String imageUrl;

    @Column(nullable = false)
    private String username;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy="feed", cascade = CascadeType.ALL)
    private Set<WorkoutTag> tags;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Feed(FeedRequestDto feedRequestDto, String imageUrl, User user) {
        this.title = feedRequestDto.getTitle();
        this.content = feedRequestDto.getContent();
        this.imageUrl = imageUrl;
        this.username = feedRequestDto.getUsername();
        this.user = user;
    }

    public void update(FeedRequestDto feedRequestDto) {
        this.title = feedRequestDto.getTitle();
        this.content = feedRequestDto.getContent();
    }
}