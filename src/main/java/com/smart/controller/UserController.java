package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
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
		System.out.println("Users " + user);

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
		System.out.println("=======>"+user.getId());
		
		  if(user.getId()==contact.getUser().getId()) 
		  { m.addAttribute("contact",contact);
		  m.addAttribute("title", contact.getName());
		  }
		 
		return "normal/contact_details";
	}
	//delete contact handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid,Model m,Principal p,HttpSession session)
	{
		String name = p.getName();
		User user = userRepo.getUserByName(name);
		Optional<Contact> optional = contactRepo.findById(cid);
		Contact contact = optional.get();
		user.getContacts().remove(contact);
		userRepo.save(user);
		session.setAttribute("message", new Message("Contact deleted Successfully", "success"));
		
		return "redirect:/user/show-contacts/0";
	}
	//update handler
	@PostMapping("/update-contact/{cid}")
	public String updateContact(@PathVariable("cid")Integer cid,Model m)
	{
		m.addAttribute("title", "Update-Contact");
		 Contact contact = contactRepo.findById(cid).get();
		 m.addAttribute("contact", contact);
		return "normal/update_contact";
	}
	
	//process update contact handler
	@PostMapping("process-update")
	public String processUpdateContact(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile f,
			Model m,
			HttpSession session,Principal p)
	{
		try {
			
			//old contact details
			Contact contactold = contactRepo.findById(contact.getcId()).get();
			//check image if he selected new img or not
			if(!f.isEmpty())
			{
				//file work
				//upload file to folder and update the name to contact
				//delete old photo
				File f1 = new ClassPathResource("static/img").getFile();
				File f2 = new File(f1,contactold.getImage());
				f2.delete();
				//update new photo
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+f.getOriginalFilename());
				Files.copy(f.getInputStream(),path , StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(f.getOriginalFilename());
			}
			else
			{
				contact.setImage(contactold.getImage());
			}
			
			User user = userRepo.getUserByName(p.getName());
			contact.setUser(user);
			contactRepo.save(contact);
			session.setAttribute("message", new Message("your contact is updated...","success"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Cname==>"+ contact.getName());
		System.out.println("CID==>"+ contact.getcId());
		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	
	//your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model m,Principal p)
	{
		m.addAttribute("title", "Your Profile");

		return "normal/profile";
	}
}
