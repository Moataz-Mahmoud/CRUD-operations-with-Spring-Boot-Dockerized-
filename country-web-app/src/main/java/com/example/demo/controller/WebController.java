package com.example.demo.controller;

import com.example.demo.Repository.CountryRepository;
import com.example.demo.model.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//The controller receives requests from client, using repository to
// update/get data and return results.
@RestController
@RequestMapping("/countries")
public class WebController {

    @Autowired
    private CountryRepository countryRepository;

    /**
     * Get a country by its code.
     * @param countryCode
     * @return the user by id
     * @throws ResourceNotFoundException if the resource is not found
     */
    @RequestMapping("/{countryCode}")
    public ResponseEntity<Country> getCountryByCode(@PathVariable(value = "countryCode") String countryCode) throws ResourceNotFoundException {
        Country country;
        country = countryRepository.findById(countryCode).orElseThrow(
                () -> new ResourceNotFoundException("User not found on :: " +
                                                    countryCode));
        return ResponseEntity.ok().body(country);
    }

    /**
     * get all the countries in the
     * @return
     */
    @GetMapping("/all")
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    /**
     * add a new country to all the countries.
     * @param country
     * @return the added country
     */
    @PostMapping("/add")
    public Country createCountry(@Valid @RequestBody Country country) {
        return countryRepository.save(country);
    }

    /**
     * Update a country with another country object sent as parameter
     * @param countryCode
     * @param countryDetails
     * @return the HTTP response of the update step
     * @throws ResourceNotFoundException if the resource is not found
     */
    @PutMapping("/{countryCode}")
    public ResponseEntity<Country> updateCountry(@PathVariable(value = "countryCode") String countryCode, @Valid @RequestBody Country countryDetails) throws ResourceNotFoundException {
        Country country =
                countryRepository.findById(countryCode).orElseThrow(() -> new ResourceNotFoundException("Country not found on :: " + countryCode));
        country.setName(countryDetails.getName());
        country.setContinent(countryDetails.getContinent());
        country.setPopulation(countryDetails.getPopulation());
        country.setRegion(countryDetails.getRegion());
        country.setSurface_area(countryDetails.getSurface_area());
        country.setGnp(countryDetails.getGnp());
        country.setLocal_name(countryDetails.getLocal_name());
        country.setGovernment_form(countryDetails.getGovernment_form());
        country.setHead_of_state(countryDetails.getHead_of_state());
        country.setCode2(countryDetails.getCode2());

        return ResponseEntity.ok(countryRepository.save(country));
    }

    /**
     * Delete a country
     * @param countryCode
     * @return a map for the success or failure of the delete process
     * @throws throws ResourceNotFoundException if the resource is not found
     */
    @DeleteMapping("/{countryCode}")
    public Map<String, Boolean> deleteCountry(@PathVariable(value = "countryCode") String countryCode) throws ResourceNotFoundException {
        Country country = countryRepository.findById(countryCode).orElseThrow(() -> new ResourceNotFoundException("Country not found on :: " + countryCode));
        countryRepository.delete(country);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
}