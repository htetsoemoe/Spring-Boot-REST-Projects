package com.jdc.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.jdc.rest.dao.CustomerDao;
import com.jdc.rest.entities.Customer;

@RestController
public class CustomerController {
	
	@Autowired
	private CustomerDao customerDao;
	
	@GetMapping("/customers")
	public Iterable<Customer> getAll() {
		return customerDao.findAll();
	}
	
	@GetMapping("/customers/{id}")
	public Customer getCustomer(@PathVariable int id) {
		return customerDao.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

}
