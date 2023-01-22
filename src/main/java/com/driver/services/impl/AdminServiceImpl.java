package com.driver.services.impl;

import java.util.List;

import com.driver.ResponseDto.AdminResponse;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Admin;
import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.repository.AdminRepository;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	AdminRepository adminRepository1;

	@Autowired
	DriverRepository driverRepository1;

	@Autowired
	CustomerRepository customerRepository1;

	@Override
	public void adminRegister(Admin admin) {
		//Save the admin in the database
//		Admin adminEntity = new Admin();
//		int adminId = admin.getAdminId();
//		String username = admin.getUsername();
//		String password = admin.getPassword();
//
//		adminEntity.setAdminId(adminId);
//		adminEntity.setUsername(username);
//		adminEntity.setPassword(password);

		adminRepository1.save(admin);
	}

	@Override
	public Admin updatePassword(Integer adminId, String password) {
		//Update the password of admin with given id
		Admin adminEntity = adminRepository1.findById(adminId).get();

		adminEntity.setPassword(password);

		adminRepository1.save(adminEntity);

		return adminEntity;

	}

	@Override
	public void deleteAdmin(int adminId){
		// Delete admin without using deleteById function
		Admin admin = adminRepository1.findById(adminId).get();
		adminRepository1.delete(admin);

	}

	@Override
	public List<Driver> getListOfDrivers() {
		//Find the list of all drivers
		List<Driver> driverList = driverRepository1.findAll();
		return driverList;

	}

	@Override
	public List<Customer> getListOfCustomers() {
		//Find the list of all customers
		List<Customer> customerList = customerRepository1.findAll();
		return customerList;

	}

}
