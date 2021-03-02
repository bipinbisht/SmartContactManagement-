package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
	
	//processing contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact 
			contact,Principal principal,
			@RequestParam("profileImage") MultipartFile file)
	{
		try {
		String name = principal.getName();
		User user = userRepo.getUserByName(name);
		//processing and uploading file..
		if(file.isEmpty())
		{
			//if file is empty try your message
			System.out.println("File is empty");
		}
		else
		{
			//upload file to folder and update the name to contact
			contact.setImage(file.getOriginalFilename());
			File saveFile = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(),path , StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Image is uploaded");
		}
		user.getContacts().add(contact);
		contact.setUser(user);
		userRepo.save(user);
		
		System.out.println(contact);
		System.out.println("Added to Database");
		}catch (Exception e) {
			System.out.println("ERROR "+e.getMessage());
			e.printStackTrace();
		}
		return "normal/add_contact_form";
	}
	
}
