package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.models.Contact;
import com.smart.models.User;

@RestController
public class SearchController {
	@Autowired
	private UserRepository userrepo;
	@Autowired
	private ContactRepository contactrepo;
	//search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query")String query,Principal p)
	{
		System.out.println(query);
		User user = userrepo.getUserByName(p.getName());
		List<Contact> contacts = contactrepo.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(contacts);
	}

}
