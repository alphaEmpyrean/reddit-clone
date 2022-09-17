package com.etherealhazel.redditclone.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.etherealhazel.redditclone.dto.PostRequest;
import com.etherealhazel.redditclone.dto.PostResponse;
import com.etherealhazel.redditclone.exception.SpringRedditException;
import com.etherealhazel.redditclone.mapper.PostMapper;
import com.etherealhazel.redditclone.model.Post;
import com.etherealhazel.redditclone.model.Subreddit;
import com.etherealhazel.redditclone.model.User;
import com.etherealhazel.redditclone.repository.PostRepository;
import com.etherealhazel.redditclone.repository.SubredditRepository;
import com.etherealhazel.redditclone.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class PostService {

    private final SubredditRepository subredditRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final PostMapper postMapper;

    public Post save(PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
            .orElseThrow(() -> new SpringRedditException(postRequest.getSubredditName() + " does not exist"));

        User currentUser = authService.getCurrentUser();

        Post newPost = postMapper.map(postRequest, subreddit, currentUser);

        return postRepository.save(newPost);
    }

    @Transactional
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new SpringRedditException("Post not found - " + id));
        return postMapper.mapToDto(post);
    }

    @Transactional
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream().map(postMapper::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {
        Subreddit subreddit = subredditRepository.findById(subredditId).orElseThrow(() -> new SpringRedditException("Subreddit not found"));
        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        return posts.stream().map(postMapper::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public List<PostResponse> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("Username not found"));
        return postRepository.findByUser(user).stream().map(postMapper::mapToDto).collect(Collectors.toList());
    }
}
