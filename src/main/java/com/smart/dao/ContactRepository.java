package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.models.Contact;
import com.smart.models.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	//currentPage-page
	//Contact Per Page-5
	@Query("from Contact as c where  c.user.id =:userId")
	public Page<Contact> findContactsByUser(@Param("userId")int userId,Pageable pageable);
	
	/*
	 * @Query("select cId from Contact where Contact.user.id=:id") public
	 * List<Contact> findByUserId(@Param("id") int id);
	 */
	
	//search
	public List<Contact> findByNameContainingAndUser(String name,User user);
}
