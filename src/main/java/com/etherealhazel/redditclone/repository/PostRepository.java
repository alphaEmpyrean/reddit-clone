package com.etherealhazel.redditclone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.etherealhazel.redditclone.model.Post;
import com.etherealhazel.redditclone.model.Subreddit;
import com.etherealhazel.redditclone.model.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{

    List<Post> findAllBySubreddit(Subreddit subreddit);

    List<Post> findByUser(User user);

}
