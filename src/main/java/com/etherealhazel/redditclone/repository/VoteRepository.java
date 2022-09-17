package com.etherealhazel.redditclone.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.etherealhazel.redditclone.model.Post;
import com.etherealhazel.redditclone.model.User;
import com.etherealhazel.redditclone.model.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);

    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);

}
