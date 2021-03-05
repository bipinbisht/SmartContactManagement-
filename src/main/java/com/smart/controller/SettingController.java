package com.smart.controller;

import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.helper.Message;
import com.smart.models.User;

@Controller
@RequestMapping("/user")
public class SettingController {

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder ;
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ContactRepository contactRepo;
	
	@ModelAttribute
	public void addCommonData(Model m, Principal p) {
		String userName = p.getName();
		System.out.println("USERNAME=:" + userName);
		// get the user using username(email)
		User user = userRepo.getUserByName(userName);
		System.out.println("Users " + user);

		m.addAttribute("user", user);
	}
	
	@GetMapping("/settings")
	public String openSetting()
	{
		return "normal/settings";
	}
	
	//change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword")String oldPassword,
			                     @RequestParam("newPassword")String newPassword,
			                     Principal p,
			                     HttpSession session)
	{
		System.out.println("newPassword"+newPassword);
		System.out.println("oldPassword"+oldPassword);
		
		String userName = p.getName();
		User user = userRepo.getUserByName(userName);
		/**now get the password**/
		String password = user.getPassword();
		if(bCryptPasswordEncoder.matches(oldPassword, password))
		{
			//change the password
			user.setPassword(bCryptPasswordEncoder.encode(newPassword));
			userRepo.save(user);
			session.setAttribute("message",new Message("Your password is updated..", "success"));
		}
		else
		{
			//error
			session.setAttribute("message",new Message("Please enter correct old password", "danger"));
			return "redirect:/user/settings";
		}
		return "redirect:/user/index";
	}
}
