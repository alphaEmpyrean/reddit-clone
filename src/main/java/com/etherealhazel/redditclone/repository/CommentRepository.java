package com.etherealhazel.redditclone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.etherealhazel.redditclone.model.Comment;
import com.etherealhazel.redditclone.model.Post;
import com.etherealhazel.redditclone.model.User;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);

    List<Comment> findByUser(User user);

}
