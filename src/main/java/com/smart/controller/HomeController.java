package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.helper.Message;
import com.smart.models.User;

@Controller
public class HomeController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@RequestMapping("")
	public String home(Model m) {
		m.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	@RequestMapping("/about")
	public String about(Model m) {
		m.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}

	@RequestMapping("/signup")
	public String signup(Model m) {
		m.addAttribute("title", "Register - Smart Contact Manager");
		m.addAttribute("user", new User());
		return "signup";
	}

	// this handler for registering user

	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult res,
	@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model m,HttpSession session) {
		try {
			if (!agreement) {
				throw new Exception(" you have not agreed the terms and conditions");	
			}
			if(res.hasErrors())
			{
				System.out.println("error "+res.toString());
				m.addAttribute("user",user);
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			User result = userRepository.save(user);
			System.out.println(user);
			m.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Registered","alert-success"));
			return "signup";
			
		} catch (Exception e) {
			e.printStackTrace();
			m.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went Wrong"+e.getMessage(),"alert-danger"));
			System.out.println();
			return "signup";
		}
		
	}

	//handler for custom login
	@GetMapping("user-login")
	public String customLogin(Model m)
	{
		m.addAttribute("title","Login Page");
		return "login";
	}
	
	@RequestMapping("login-fail")
	public String loginFail(Model m)
	{
		m.addAttribute("title","Login Fail");
		return "login-fail";
	}
}
