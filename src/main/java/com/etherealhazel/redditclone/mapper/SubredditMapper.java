package com.etherealhazel.redditclone.mapper;

import java.util.List;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.etherealhazel.redditclone.dto.SubredditDto;
import com.etherealhazel.redditclone.model.Post;
import com.etherealhazel.redditclone.model.Subreddit;
import com.etherealhazel.redditclone.model.User;

@Mapper(componentModel = "spring")
public interface SubredditMapper {
    
    @Mapping(target = "numberOfPosts", expression = "java(mapPosts(subreddit.getPosts()))")
    SubredditDto mapSubredditToDto(Subreddit subreddit);

    default Integer mapPosts(List<Post> numberOfPosts) { return numberOfPosts.size(); }

    @InheritInverseConfiguration
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "id", source = "subredditDto.id")
    Subreddit mapDtoToSubreddit(SubredditDto subredditDto, User user);

}
