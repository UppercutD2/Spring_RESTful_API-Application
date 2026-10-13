package somePackage.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import somePackage.Util.PersonNotFoundException;
import somePackage.models.Person;
import somePackage.repositories.PeopleRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> findAll() {

        System.out.println("Sevice has been reached.");
        return peopleRepository.findAll();
    }

    public Person findOne(int id) {
        Optional<Person> foundPerson = peopleRepository.findById(id);
        return foundPerson.orElseThrow(PersonNotFoundException::new);
    }


    @Transactional
    public void save(Person person)
    {
            enrichPerson(person);
        System.out.println("Person enriched.");
        peopleRepository.save(person);
    }

    private void enrichPerson(Person person)
    {

        person.setCreatedAt(LocalDateTime.now());
        person.setUpdatedAt(LocalDateTime.now());
        person.setCreatedBy("Postman");
    }

}