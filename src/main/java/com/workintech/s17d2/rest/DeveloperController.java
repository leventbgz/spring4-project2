package com.workintech.s17d2.rest;

import com.workintech.s17d2.dto.DeveloperResponse;
import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workintech/developers")
public class DeveloperController {
    public Map<Integer, Developer> developers;
    private Taxable taxable;

    @Autowired
    public DeveloperController(Taxable taxable){
        this.taxable = taxable;
    }

    @PostConstruct
    public void init(){
        developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> getAllDevelopers(){
        return developers.values().stream().toList();
    }

    @GetMapping("/{id}")
    public Developer getDeveloperById(@PathVariable("id") Integer id) throws Exception {
        if (developers.containsKey(id)){
            return developers.get(id);
        } else {
            throw new Exception("Developer could not be found");
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeveloperResponse saveNewDeveloper(@RequestBody Developer developerToBeSaved) {
        Developer createdDeveloper = null;
        double tax = 0;

        switch (developerToBeSaved.getExperience()) {
            case JUNIOR:
                tax = developerToBeSaved.getSalary() * taxable.getSimpleTaxRate() / 100;
                createdDeveloper = new JuniorDeveloper(developerToBeSaved.getId(),
                        developerToBeSaved.getName(),
                        developerToBeSaved.getSalary() - tax);
                break;
            case MID:
                tax = developerToBeSaved.getSalary() * taxable.getMiddleTaxRate() / 100;
                createdDeveloper = new MidDeveloper(developerToBeSaved.getId(),
                        developerToBeSaved.getName(),
                        developerToBeSaved.getSalary() - tax);
                break;
            case SENIOR:
                tax = developerToBeSaved.getSalary() * taxable.getUpperTaxRate() / 100;
                createdDeveloper = new SeniorDeveloper(developerToBeSaved.getId(),
                        developerToBeSaved.getName(),
                        developerToBeSaved.getSalary() - tax);
                break;
        }

        if (createdDeveloper != null) {
            developers.put(createdDeveloper.getId(), createdDeveloper);
        }
        return new DeveloperResponse(createdDeveloper, HttpStatus.CREATED.value(), "Developer has been created succesfully.");
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DeveloperResponse updateDeveloper(@PathVariable("id") Integer id, @RequestBody Developer developerToBeUpdated) {
        developerToBeUpdated.setId(id);

        Developer updatedDeveloper = null;
        double tax;

        switch (developerToBeUpdated.getExperience()) {
            case JUNIOR:
                tax = developerToBeUpdated.getSalary() * taxable.getSimpleTaxRate() / 100;
                updatedDeveloper = new JuniorDeveloper(
                        developerToBeUpdated.getId(),
                        developerToBeUpdated.getName(),
                        developerToBeUpdated.getSalary() - tax
                );
                break;
            case MID:
                tax = developerToBeUpdated.getSalary() * taxable.getMiddleTaxRate() / 100;
                updatedDeveloper = new MidDeveloper(
                        developerToBeUpdated.getId(),
                        developerToBeUpdated.getName(),
                        developerToBeUpdated.getSalary() - tax
                );
                break;
            case SENIOR:
                tax = developerToBeUpdated.getSalary() * taxable.getUpperTaxRate() / 100;
                updatedDeveloper = new SeniorDeveloper(
                        developerToBeUpdated.getId(),
                        developerToBeUpdated.getName(),
                        developerToBeUpdated.getSalary() - tax
                );
                break;
            default:
                throw new IllegalArgumentException("Invalid experience level: " + developerToBeUpdated.getExperience());
        }

        if (updatedDeveloper != null) {
            developers.put(updatedDeveloper.getId(), updatedDeveloper);
        }

        return new DeveloperResponse(updatedDeveloper, HttpStatus.OK.value(), "Developer with id " + id + " has been updated succesfully");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DeveloperResponse deleteDeveloperById(@PathVariable("id") Integer id){
        Developer removedDeveloper = developers.remove(id);
        return new DeveloperResponse(removedDeveloper, HttpStatus.OK.value(), "Developer with id " + id + " has been deleted succesfully");
    }
}
