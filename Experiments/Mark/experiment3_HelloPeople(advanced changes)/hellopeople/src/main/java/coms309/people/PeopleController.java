package coms309.people;

import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Controller used to showcase Create and Read from a LIST
 *
 * @author Vivek Bengre
 */

@RestController
public class PeopleController {

    // Note that there is only ONE instance of PeopleController in 
    // Springboot system.
    HashMap<String, Person> peopleList = new  HashMap<>();

    //CRUDL (create/read/update/delete/list)
    // use POST, GET, PUT, DELETE, GET methods for CRUDL

    // THIS IS THE LIST OPERATION
    // gets all the people in the list and returns it in JSON format
    // This controller takes no input. 
    // Springboot automatically converts the list to JSON format 
    // in this case because of @ResponseBody
    // Note: To LIST, we use the GET method
    @GetMapping("/people")
    public  HashMap<String,Person> getAllPersons() {
        return peopleList;
    }

    // THIS IS THE CREATE OPERATION
    // springboot automatically converts JSON input into a person object and 
    // the method below enters it into the list.
    // It returns a string message in THIS example.
    // Note: To CREATE we use POST method
    @PostMapping("/people")
    public  String createPerson(@RequestBody Person person) {
        System.out.println(person);
        peopleList.put(person.getFirstName(), person);
        String s = "New person "+ person.getFirstName() + " Saved";
        return s;
        //public  ResponseEntity<Map<String, String>>  //unused
        // createPerson(@RequestBody Person person) { // unused
        //Map <String, String> body = new HashMap<>();// unused
        //body.put("message", s); // unused
        //ResponseEntity<>(body, HttpStatus.OK); // unused
    }

    // THIS IS THE READ OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We extract the person from the HashMap.
    // springboot automatically converts Person to JSON format when we return it
    // Note: To READ we use GET method
    @GetMapping("/people/{firstName}")
    public Person getPerson(@PathVariable String firstName) {
        Person p = peopleList.get(firstName);
        return p;
    }

    // THIS IS A GET METHOD
    // RequestParam is expected from the request under the key "name"
    // returns all names that contains value passed to the key "name"
    @GetMapping("/people/contains/name")
    public List<Person> getPersonByParam(@RequestParam("firstName") String name) {
        List<Person> res = new ArrayList<>();
        for (Person p : peopleList.values()) {
            if (p.getFirstName().contains(name) || p.getLastName().contains(name))
                res.add(p);

        }
        return res;
    }

    //added a new method to search my phone number
    // THIS IS A GET METHOD
    // RequestParam is expected from the request under the key "number"
    // returns all names that contains value passed to the key "number"
    @GetMapping("/people/contains/number")
    public List<Person> getPersonByParamNum(@RequestParam("phone") String number) {
        List<Person> res = new ArrayList<>();
        for (Person p : peopleList.values()) {
            if (p.getTelephone().contains(number))
                res.add(p);
        }
        return res;
    }

    // THIS IS THE UPDATE OPERATION
    // We extract the person from the HashMap and modify it.
    // Springboot automatically converts the Person to JSON format
    // Springboot gets the PATHVARIABLE from the URL
    // Here we are returning what we sent to the method
    // Note: To UPDATE we use PUT method
    @PutMapping("/people/{firstName}")
    public Person updatePerson(@PathVariable String firstName, @RequestBody Person p) {
        peopleList.replace(firstName, p);
        return peopleList.get(firstName);
    }


    // THIS IS THE DELETE OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We return the entire list -- converted to JSON
    // Note: To DELETE we use delete method
    
    @DeleteMapping("/people/{firstName}")
    public HashMap<String, Person> deletePerson(@PathVariable String firstName) {
        peopleList.remove(firstName);
        return peopleList;
    }

    //created a GET operation
    //Used to count the number of people added to the arraylist
    @GetMapping("/people/count")
    public int getPeopleSize(){
        return peopleList.size();}



    //Modified section of code
    List<Person> classroom = new ArrayList<>();

    @PostMapping("/classroom/add/{firstName}")
    public String addToClassroom(@PathVariable String firstName) {
        Person p = peopleList.get(firstName);

        if (p == null) {
            return "person does not exist";
        }

        if (p.getRole().equals("student") || p.getRole().equals("Student")) {
            classroom.add(p);
            return p.getFirstName() + " has joined the class";
        }

        if (p.getRole().equals("Professor") || p.getRole().equals("professor")) {
            int professorCount = 0;
            for (Person person : classroom) {
                if (person.getRole().equals("professor") || person.getRole().equals("Professor")) {
                    professorCount++;
                }
            }
            //Limits the number of professors in a classroom to 2.
            if (professorCount >= 2) {
                return "The maximum number of professors is and " + p.getFirstName() + "can not join";
            }
            classroom.add(p);
            return p.getFirstName() + " is now a proffesor in the class";
        }
        //For other roles
        return "Only students and professors can join the classroom.";
    }

    @GetMapping("/classroom")
    public List<Person> getClassroom() {
        return classroom;
    }

    @GetMapping("/classroom/count")
    public HashMap<String, Integer> countClassroomMembers()
    {
        int students = 0;
        int professors = 0;

        for(Person p : classroom){
            if(p.getRole().equals("student") || p.getRole().equals("Student")){
                students++;
            } else if(p.getRole().equals("professor") || p.getRole().equals("Professor")) {
                professors++;
            }
            }

        HashMap<String, Integer> result = new HashMap<>();
        result.put("students", students);
        result.put("professors", professors);
        return result;
        }
} // end of people controller

