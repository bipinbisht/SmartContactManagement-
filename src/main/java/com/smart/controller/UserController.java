package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.helper.Message;
import com.smart.models.Contact;
import com.smart.models.User;

@Controller
@RequestMapping("user")
public class UserController {
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
			@RequestParam("profileImage") MultipartFile file,
			HttpSession httpSession)
	{
		try {
		String name = principal.getName();
		User user = userRepo.getUserByName(name);
		//processing and uploading file..
		if(file.isEmpty())
		{
			//if file is empty try your message
			System.out.println("File is empty");
			contact.setImage("contact.png");
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
		//message success
		httpSession.setAttribute("message", new Message("Contact Added Successfully..", "success"));
		}catch (Exception e) {
			System.out.println("ERROR "+e.getMessage());
			e.printStackTrace();
			//message error
			httpSession.setAttribute("message", new Message("Contact Failed To Add..", "danger"));
		}
		return "normal/add_contact_form";
	}
	//show contacts handler
	//per page=5[n]
	//current page=0[page]
	@GetMapping("show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model m,Principal p)
	{
		 m.addAttribute("title", "Show User Contacts");
		 
		 String userName = p.getName();
		 User user = userRepo.getUserByName(userName);
		 //for pagenation
		 Pageable pageable = PageRequest.of(page, 5);
		 Page<Contact> contacts = contactRepo.findContactsByUser(user.getId(),pageable);
		 m.addAttribute("contacts", contacts);
		 m.addAttribute("currentPage", page);
		 m.addAttribute("totalPages", contacts.getTotalPages());
		 return "normal/show_contacts";
	}
	
	
	//showing particular contact details
	@GetMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") int cId,Model m,Principal p)
	{
		Optional<Contact> optional = contactRepo.findById(cId);
		Contact contact = optional.get();
		//check
		String userName = p.getName();
		User user = userRepo.getUserByName(userName);
		System.out.println("====>"+user.getId());
		
		  if(user.getId()==contact.getUser().getId()) 
		  { m.addAttribute("contact",contact);
		  m.addAttribute("title", contact.getName());
		  }
		 
		return "normal/contact_details";
	}
	
}
