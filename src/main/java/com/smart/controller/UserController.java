package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.UserRepository;
import com.smart.models.Contact;
import com.smart.models.User;

@Controller
@RequestMapping("user")
public class UserController {
	@Autowired
	private UserRepository userRepo;

	@ModelAttribute
	public void addCommonData(Model m, Principal p) {
		String userName = p.getName();
		System.out.println("USERNAME=:" + userName);
		// get the user using username(email)
		User user = userRepo.getUserByName(userName);
		System.out.println("Userss " + user);

		m.addAttribute("user", user);
	}

	
	@RequestMapping("index")
	public String dashBoard(Model m, Principal p) {
		m.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	
	// add form handler
	@GetMapping("add-contact")
	public String openAddContactForm(Model m) {
		m.addAttribute("title", "Add Contact");
		m.addAttribute("contact", new Contact());

		return "normal/add_contact_form";
	}
	
}
