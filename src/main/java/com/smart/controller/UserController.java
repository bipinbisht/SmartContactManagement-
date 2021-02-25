package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.UserRepository;
import com.smart.models.User;

@Controller
@RequestMapping("user")
public class UserController {
	@Autowired
	private UserRepository userRepo;
	@RequestMapping("index")
	public String dashBoard(Model m,Principal p)
	{
		String userName = p.getName();
		System.out.println("USERNAME=:"+userName);
		//get the user using username(email)
		User user = userRepo.getUserByName(userName);
		System.out.println("Userss "+user);
		
		m.addAttribute("user", user); 
		return "normal/user_dashboard";
	}
}
