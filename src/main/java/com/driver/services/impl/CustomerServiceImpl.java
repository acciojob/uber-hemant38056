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
		List<TripBooking> tripBookingList = customer.getTripBookingList();
		for (TripBooking tripBooking : tripBookingList){
			if(tripBooking.getStatus() == TripStatus.CONFIRMED){
				tripBooking.setStatus(TripStatus.CANCELED);
			}
		}
		customerRepository2.delete(customer);

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query

//		int freeDriverId = 0;
//		List<Driver> driverList = driverRepository2.findAll();
//		for(Driver driver : driverList){
//			Cab cab = driver.getCab();
//			if(cab.getAvailable() == true){
//				freeDriverId = driver.getDriverId();
//				break;
//			}
//		}
//		if(freeDriverId == 0){
//			throw new Exception("No cab available!");
//		}
//
//			TripBooking tripBooking = new TripBooking();
//			tripBooking.setFromLocation(fromLocation);
//			tripBooking.setToLocation(toLocation);
//			tripBooking.setDistanceInKm(distanceInKm);
//
//			tripBookingRepository2.save(tripBooking);
//
//			Customer customer = customerRepository2.findById(customerId).get();
//			customer.getTripBookingList().add(tripBooking);
//
//			customerRepository2.save(customer);
//
//			Driver driver = driverRepository2.findById(freeDriverId).get();
//			driver.getTripBookingList().add(tripBooking);
//			Cab cab = driver.getCab();
//			cab.setAvailable(false);
//
//			tripBooking.setStatus(TripStatus.CONFIRMED);
//
//			driverRepository2.save(driver);
//
//
//
//			return tripBooking;


		TripBooking tripBooking = new TripBooking();
		Driver driver = null;

		List<Driver> allDrivers = driverRepository2.findAll();

		for(Driver driver1 : allDrivers){
			if(driver1.getCab().getAvailable() == true){
				if((driver == null) || (driver.getDriverId() > driver1.getDriverId())){
					driver = driver1;
				}
			}
		}

		if(driver == null){
			throw new Exception("No cab available!");
		}

		Customer customer = customerRepository2.findById(customerId).get();
		tripBooking.setCustomer(customer);
		tripBooking.setDriver(driver);
		driver.getCab().setAvailable(Boolean.FALSE);
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDistanceInKm(distanceInKm);

		int ratePerKm = driver.getCab().getPerKmRate();

		tripBooking.setBill(distanceInKm*10);

		tripBooking.setStatus(TripStatus.CONFIRMED);

		customer.getTripBookingList().add(tripBooking);
		customerRepository2.save(customer);

		driver.getTripBookingList().add(tripBooking);
		driverRepository2.save(driver);


		return tripBooking;

	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.CANCELED);
		tripBooking.setBill(0);
		tripBooking.getDriver().getCab().setAvailable(true);
		tripBookingRepository2.save(tripBooking);

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.COMPLETED);
		int totalDistance = tripBooking.getDistanceInKm();

		Driver driver = tripBooking.getDriver();
		Cab cab = driver.getCab();
		int price = cab.getPerKmRate();

		tripBooking.setBill(totalDistance * price);

		tripBooking.getDriver().getCab().setAvailable(true);

		tripBookingRepository2.save(tripBooking);
	}
}
