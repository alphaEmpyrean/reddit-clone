package com.etherealhazel.redditclone.mapper;

import java.time.temporal.ChronoUnit;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.etherealhazel.redditclone.dto.CommentDto;
import com.etherealhazel.redditclone.model.Comment;
import com.etherealhazel.redditclone.model.Post;
import com.etherealhazel.redditclone.model.User;
import com.etherealhazel.redditclone.service.TimeAgo;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "commentDto.text")
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "post", source = "post")
    @Mapping(target = "user", source = "user")
    public abstract Comment map(CommentDto commentDto, Post post, User user);

    @Mapping(target = "postId", expression = "java(comment.getPost().getId())")
    @Mapping(target = "userName", expression = "java(comment.getUser().getUsername())")
    @Mapping(target = "duration", expression = "java(getDuration(comment))")
    public abstract CommentDto mapToDto(Comment comment);

    public String getDuration(Comment comment) { return TimeAgo.using(comment.getCreatedDate().until(java.time.Instant.now(), ChronoUnit.MILLIS)); }
}
