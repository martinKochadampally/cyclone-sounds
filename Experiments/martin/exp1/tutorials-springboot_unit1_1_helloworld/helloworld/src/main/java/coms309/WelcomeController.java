package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

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
        return "Hi " + name + "! Welcome to Cyclone Sounds!!! Here are your songs: ";
    }

    @GetMapping("/counter")
    public String counter() {
        return "Please enter a number after counter in url. For example 'https://.../counter/10'";
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
