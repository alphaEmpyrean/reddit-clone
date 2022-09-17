package com.etherealhazel.redditclone.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etherealhazel.redditclone.dto.SubredditDto;
import com.etherealhazel.redditclone.exception.SpringRedditException;
import com.etherealhazel.redditclone.mapper.SubredditMapper;
import com.etherealhazel.redditclone.model.Subreddit;
import com.etherealhazel.redditclone.model.User;
import com.etherealhazel.redditclone.repository.SubredditRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SubredditService {
	
	private final SubredditRepository subredditRepository;
	private final SubredditMapper subredditMapper;
	private final AuthService authService;

	@Transactional
	public SubredditDto save(SubredditDto subredditDto) {
		User currentUser = authService.getCurrentUser();
		Subreddit save = subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto, currentUser));
		subredditDto.setId(save.getId());
		return subredditDto;
	}

	@Transactional(readOnly = true)
	public List<SubredditDto> getAll() {
		return subredditRepository.findAll()
		.stream()
		.map(subredditMapper::mapSubredditToDto)
		.collect(Collectors.toList());
	}

	public SubredditDto getSubreddit(Long id) {
		Subreddit subreddit = subredditRepository.findById(id).orElseThrow(() -> new SpringRedditException("No subreddit found with id" + id));
		return subredditMapper.mapSubredditToDto(subreddit);
	}
}
