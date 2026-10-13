package somePackage.controllers;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import somePackage.Util.PersonErrorResponse;
import somePackage.Util.PersonNotCreatedException;
import somePackage.Util.PersonNotFoundException;
import somePackage.dto.PersonDTO;
import somePackage.models.Person;
import somePackage.services.PeopleService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/people")
public class PeopleController {

    private final ModelMapper mapper;
    private final PeopleService peopleService;

    @Autowired
    public PeopleController(ModelMapper mapper, PeopleService peopleService) {
        this.mapper = mapper;
        this.peopleService = peopleService;
    }

    @GetMapping()
    public List<PersonDTO>  getPeople()
    {
        System.out.println("Controller has been reached.");
        List<PersonDTO> people = peopleService.findAll().stream().map(this::personToPersonDTO).collect(Collectors.toList());

            people.forEach(System.out::println);
        return people; ///Jasckson , aka JSON automatically converts list<Person> into JSON format.
    }


    @GetMapping("/{id}")
    public PersonDTO getPerson(@PathVariable ("id") int id){
        return personToPersonDTO(peopleService.findOne(id));
    }

    @ExceptionHandler(PersonNotFoundException.class)
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e)
    {
            PersonErrorResponse response = new PersonErrorResponse(
                    "Person with mentioned ID wasn't found.",
                    System.currentTimeMillis());

            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }



    @PostMapping()
    public ResponseEntity<HttpStatus> createPerson(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult)
    {
                if(bindingResult.hasErrors()) {
                    StringBuilder errorMsg = new StringBuilder();

                    List<FieldError> errors = bindingResult.getFieldErrors();


                    for( FieldError fError : errors)
                    {
                        errorMsg
                                .append(fError.getField())
                                .append("-")
                                .append(fError.getDefaultMessage())
                                .append("; ");
                    }



                    throw new PersonNotCreatedException(errorMsg.toString());
                }
        System.out.println("Controller reached.");
                peopleService.save(personDTOToPerson(personDTO));


        System.out.println("Controller Passed.");
                return ResponseEntity.ok(HttpStatus.OK );

    }

    private Person personDTOToPerson(PersonDTO personDTO)
    {

        return this.mapper.map(personDTO,Person.class);
    }


    private PersonDTO personToPersonDTO(Person person)
    {
        return mapper.map(person, PersonDTO.class);

    }

    @ExceptionHandler(PersonNotCreatedException.class)
    public ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e)
    {
            PersonErrorResponse errorResponse = new PersonErrorResponse(

                    e.getMessage(),
                    System.currentTimeMillis());

            return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);


    }

}
