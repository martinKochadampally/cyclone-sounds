package coms309.user;

import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Controller used to showcase Create and Read from a LIST
 *
 * @author Martin Kochadampally
 */

@RestController
public class UserController {

    HashMap<String, User> userList = new  HashMap<>();

    @GetMapping("/user")
    public  HashMap<String,User> getAllUsers() {
        return userList;
    }

    @PostMapping("/user")
    public  String createUser(@RequestBody User user) {
        System.out.println(user);
        if (userList.putIfAbsent(user.getEmail(), user) == null)
            return "New user "+ user.getFirstName() + " Saved";
        return "New user not saved. This email already in use.";
    }

    @GetMapping("/user/{email}")
    public User getUser(@PathVariable String email) {
        User u = userList.get(email);
        return u;
    }

    // THIS IS A GET METHOD
    // RequestParam is expected from the request under the key "name"
    // returns all names that contains value passed to the key "name"
    @GetMapping("/user/contains/name")
    public List<User> getUserByName(@RequestParam("name") String name) {
        List<User> res = new ArrayList<>();
        for (User u : userList.values()) {
            if (u.getFirstName().contains(name) || u.getLastName().contains(name))
                res.add(u);
        }
        return res;
    }


    @PutMapping("/user/{email}")
    public User updateUser(@PathVariable String email, @RequestBody User u) {
        userList.replace(email, u);
        return userList.get(email);
    }

    @PutMapping("/user/phoneNo")
    public User updatePhoneNumber(@RequestParam("oldPhoneNo") String oldPhoneNo, @RequestParam("newPhoneNo") String newPhoneNo) {
        for (User u : userList.values()) {
            if (u.getTelephone().equals(oldPhoneNo)) {
                u.setTelephone(newPhoneNo);
                return u;
            }
        }
        return null;
    }

    // THIS IS THE DELETE OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We return the entire list -- converted to JSON
    // in this case because of @ResponseBody
    // Note: To DELETE we use delete method

    @DeleteMapping("/user/{email}")
    public HashMap<String, User> deleteUser(@PathVariable String email) {
        userList.remove(email);
        return userList;
    }
}

