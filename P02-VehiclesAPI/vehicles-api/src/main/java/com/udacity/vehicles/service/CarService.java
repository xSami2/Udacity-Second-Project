package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {




    @Autowired
    @Qualifier("pricing")
    private WebClient.Builder webClientPricing;

    @Autowired
    @Qualifier("maps")
    private WebClient webClientMaps;

    @Autowired
    ModelMapper modelMapper;
    private final CarRepository repository;

    public CarService(CarRepository repository) {
        this.repository = repository;
    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return repository.findAll();
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {


       Car car = repository.findById(id)
               .orElseThrow( () -> new CarNotFoundException("Unable to find a car with id : "+ id));


        PriceClient priceClient = new PriceClient((WebClient) webClientPricing);
        MapsClient mapsClient   = new MapsClient(webClientMaps, modelMapper);

        car.setPrice(priceClient.getPrice(id));
        car.setLocation(mapsClient.getAddress(car.getLocation()));

        return car;


    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return repository.save(car);
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        try{
            repository.deleteById(id);
        }catch (Exception e){
            throw new CarNotFoundException("Unable to find a car with id : "+ id);
        }


    }
}
