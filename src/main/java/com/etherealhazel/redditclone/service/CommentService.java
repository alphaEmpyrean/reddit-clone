package com.etherealhazel.redditclone.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.etherealhazel.redditclone.dto.CommentDto;
import com.etherealhazel.redditclone.exception.SpringRedditException;
import com.etherealhazel.redditclone.mapper.CommentMapper;
import com.etherealhazel.redditclone.model.Comment;
import com.etherealhazel.redditclone.model.NotificationEmail;
import com.etherealhazel.redditclone.model.Post;
import com.etherealhazel.redditclone.model.User;
import com.etherealhazel.redditclone.repository.CommentRepository;
import com.etherealhazel.redditclone.repository.PostRepository;
import com.etherealhazel.redditclone.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;
    private final UserRepository userRepository;

    public void save(CommentDto commentDto) {
        Post post = postRepository.findById(commentDto.getPostId()).orElseThrow(() -> new SpringRedditException("Post not found"));
        Comment comment = commentMapper.map(commentDto, post, authService.getCurrentUser());
        commentRepository.save(comment);

        String message = mailContentBuilder.build(post.getUser().getUsername() + " posted a comment on your post." + post.getUrl());
        sendCommentNotification(message, post.getUser());

    }

    private void sendCommentNotification(String message, User user) {
        mailService.sendMail(new NotificationEmail(user.getUsername() + "Commented on your post", user.getEmail(), message));
    }

    public List<CommentDto> getAllCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new SpringRedditException("Post not found"));
        return commentRepository.findByPost(post).stream().map(commentMapper::mapToDto).collect(Collectors.toList());
    }

    public List<CommentDto> getAllCommentForUser(String userName) {
        User user = userRepository.findByUsername(userName)
            .orElseThrow(() -> 
            new SpringRedditException("User not found"));
        return commentRepository.findAllByUser(user)
            .stream().map(commentMapper::mapToDto)
            .collect(Collectors.toList());
    }
    
}
