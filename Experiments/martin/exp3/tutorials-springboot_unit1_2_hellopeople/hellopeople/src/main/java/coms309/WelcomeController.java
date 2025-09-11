package coms309;

import org.springframework.web.bind.annotation.*;

/**
 * Simple Hello World Controller to display the string returned
 *
 * @author Martin Kochadampally
 */

@RestController
class WelcomeController {

    // the @GetMapping tells us that this function is supposed to read from
    // the server and output a value accordingly.
    @GetMapping("/")
    public String welcome() { // This assumes that since there is no name the user is not signed in.
        return "Welcome to Cyclone Sounds! \n To access your songs, please sign in or make an account!";
    }

    // Uses the name given in the URL to welcome the user.
    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return "Hi " + name + "! Welcome to Cyclone Sounds!!!\n Here are your songs: ";
    }

    @GetMapping("/counter/{number}")
    public String counterWithNumber(@PathVariable int number) {
        String s = "";
        for (int i = 0; i < number; i++) {
            s += (i + 1) + " ";
        }
        return s;
    }

}
