package com.etherealhazel.redditclone.mapper;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.etherealhazel.redditclone.dto.PostRequest;
import com.etherealhazel.redditclone.dto.PostResponse;
import com.etherealhazel.redditclone.model.Post;
import com.etherealhazel.redditclone.model.Subreddit;
import com.etherealhazel.redditclone.model.User;
import com.etherealhazel.redditclone.model.Vote;
import com.etherealhazel.redditclone.model.VoteType;
import com.etherealhazel.redditclone.repository.CommentRepository;
import com.etherealhazel.redditclone.repository.VoteRepository;
import com.etherealhazel.redditclone.service.AuthService;
import com.etherealhazel.redditclone.service.TimeAgo;

@Mapper(componentModel = "spring")
public abstract class PostMapper {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private AuthService authService;


    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source = "postRequest.description")
    @Mapping(target = "voteCount", constant = "0")
    @Mapping(target = "subreddit", source = "subreddit")
    @Mapping(target = "id", source = "postRequest.id")
    @Mapping(target = "user", source = "user")
    public abstract Post map(PostRequest postRequest, Subreddit subreddit, User user);

    @Mapping(target = "subredditName", source = "subreddit.name")
    @Mapping(target = "userName", source = "user.username")
    @Mapping(target = "commentCount", expression = "java(commentCount(post))")
    @Mapping(target = "duration", expression = "java(getDuration(post))")
    @Mapping(target = "upVote", expression = "java(isPostUpVoted(post))")
    @Mapping(target = "downVote", expression = "java(isPostDownVoted(post))")
    public abstract PostResponse mapToDto(Post post);

    Integer commentCount(Post post) { return commentRepository.findByPost(post).size(); }

    String getDuration(Post post) { return TimeAgo.using(post.getCreatedDate().until(java.time.Instant.now(), ChronoUnit.MILLIS)); }

    boolean isPostUpVoted(Post post) {
        return checkVoteType(post, VoteType.UPVOTE);
    }

    boolean isPostDownVoted(Post post) {
        return checkVoteType(post, VoteType.DOWNVOTE);
    }

    private boolean checkVoteType(Post post, VoteType voteType) {
        if (authService.isLoggedIn()) {
            Optional<Vote> voteForPostByUser =
                    voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post,
                            authService.getCurrentUser());
            return voteForPostByUser.filter(vote -> vote.getVoteType().equals(voteType))
                    .isPresent();
        }
        return false;
    }

}
