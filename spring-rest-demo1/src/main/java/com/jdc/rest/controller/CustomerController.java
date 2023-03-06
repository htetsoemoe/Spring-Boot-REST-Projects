package com.jdc.rest.controller;

import java.net.URI;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	
	@PostMapping("/customers")
	public ResponseEntity<Customer> create(@RequestBody Customer customer, BindingResult bindingResult) {		
		if (!bindingResult.hasErrors()) {
			Customer savedCustomer = customerDao.save(customer);
			
			return ResponseEntity
					.created(URI.create("/customers/" + savedCustomer.getId()))
					.body(savedCustomer);
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Request contains incorrect data [%s]", 
					getError(bindingResult)));
		}
	}
	
	@PutMapping("/customers/{id}")
	public ResponseEntity<Customer> update(@RequestBody Customer customer, @PathVariable int id, BindingResult bindingResult) {
		if (!bindingResult.hasErrors()) {
			
			if (customerDao.existsById(id)) {
				// explicit update
				customer.setId(id);
				Customer savedCustomer = customerDao.save(customer);
				
				return ResponseEntity.ok(savedCustomer);
			} else {
				return ResponseEntity.notFound().build();// Build the response entity with no body.
			}
			
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
					String.format("Request contains incorrect data [%s}", getError(bindingResult)));
		}
	}
	
	@DeleteMapping("/customers/{id}")
	public ResponseEntity delete(@PathVariable Integer id) {
		if (customerDao.existsById(id)) {
			customerDao.deleteById(id);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	private String getError(BindingResult bindingResult) {
		return bindingResult.getAllErrors().stream()
				.map(ObjectError::getDefaultMessage)
				.collect(Collectors.joining(", "));
	}

}
