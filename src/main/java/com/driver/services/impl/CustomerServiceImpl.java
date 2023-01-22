package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
//		Customer customerEntity = new Customer();
//		int id = customer.getCustomerId();
//		String mobile = customer.getMobile();
//		String password = customer.getPassword();
//
//		customerEntity.setCustomerId(id);
//		customerEntity.setMobile(mobile);
//		customerEntity.setPassword(password);

		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		Customer customer = customerRepository2.findById(customerId).get();
		customerRepository2.delete(customer);

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query

		int freeDriverId = 0;
		List<Driver> driverList = driverRepository2.findAll();
		for(Driver driver : driverList){
			Cab cab = driver.getCab();
			if(cab.getAvailable() == true){
				freeDriverId = driver.getDriverId();
				break;
			}
		}
		if(freeDriverId == 0){
			throw new Exception("No cab available!");
		}

		TripBooking tripBooking = new TripBooking();
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDistanceInKm(distanceInKm);

		tripBookingRepository2.save(tripBooking);

		Customer customer = customerRepository2.findById(customerId).get();
		customer.getTripBookingList().add(tripBooking);

		customerRepository2.save(customer);

		Driver driver = driverRepository2.findById(freeDriverId).get();
		driver.getTripBookingList().add(tripBooking);
		Cab cab = driver.getCab();
		cab.setAvailable(false);

		tripBooking.setStatus(TripStatus.CONFIRMED);

		driverRepository2.save(driver);



		return tripBooking;


	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.CANCELED);
		tripBookingRepository2.save(tripBooking);

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.COMPLETED);

		tripBookingRepository2.save(tripBooking);
	}
}
