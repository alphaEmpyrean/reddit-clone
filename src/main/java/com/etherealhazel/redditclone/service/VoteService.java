package com.etherealhazel.redditclone.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.etherealhazel.redditclone.dto.VoteDto;
import com.etherealhazel.redditclone.exception.SpringRedditException;
import com.etherealhazel.redditclone.model.Post;
import com.etherealhazel.redditclone.model.Vote;
import com.etherealhazel.redditclone.model.VoteType;
import com.etherealhazel.redditclone.repository.PostRepository;
import com.etherealhazel.redditclone.repository.VoteRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    @Transactional
    public void vote(VoteDto voteDto) {
        Post post = postRepository.findById(voteDto.getPostId()).orElseThrow(() -> new SpringRedditException("Post not found"));
        Optional<Vote> voteByPostAndUser = voteRepository.findByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());
        if (voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())) { 
            throw new SpringRedditException("You have alread voted " + voteDto.getVoteType());
        }
        if (VoteType.UPVOTE.equals(voteDto.getVoteType())) {
            post.setVoteCount(voteByPostAndUser.isPresent() ? post.getVoteCount() + 2 : post.getVoteCount() + 1);
        } else {
            post.setVoteCount(voteByPostAndUser.isPresent() ? post.getVoteCount() - 2 : post.getVoteCount() - 1);
        }

        voteRepository.save(mapToVote(voteDto, post));
        postRepository.save(post);
    }

    private Vote mapToVote(VoteDto voteDto, Post post) {
        return Vote.builder()
            .voteType(voteDto.getVoteType())
            .post(post)
            .user(authService.getCurrentUser())
            .build();
    }

}
