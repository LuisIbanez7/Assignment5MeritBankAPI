package com.assignments.assignment5.services;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assignments.assignment5.models.MyUserDetails;
import com.assignments.assignment5.models.User;
import com.assignments.assignment5.repository.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String userName) {
		//return new User("foo", "foo", new ArrayList<>());
		Optional<User> user = userRepository.findByUsername(userName);
		
		user.orElseThrow(() -> new UsernameNotFoundException("User not found " + userName));
		
		return user.map(MyUserDetails::new).get();
	}
//	public User save(MyUserDetails user) {
//		User newUser = new User();
//		newUser.(user.getUsername());
//		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
//		return userDao.save(newUser);
//	}
}
