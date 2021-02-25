package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.dao.UserRepository;
import com.smart.models.User;

public class UserDetailServiceImpl implements UserDetailsService{

	@Autowired
	private UserRepository userrepo;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userByName = userrepo.getUserByName(username);
		if(userByName==null)
		{
			throw new UsernameNotFoundException("could not found user");
		}
		CustomUserDetails customUserDetails = new CustomUserDetails(userByName);
		return customUserDetails ;
	}

}
