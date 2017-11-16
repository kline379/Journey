package frontend;

import javax.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import backend.User;
import backend.UserService;

@Controller
public class RegistrationController {

  @Autowired
  private UserService userService;

  @RequestMapping(value = "/register", method = RequestMethod.GET)
  public ModelAndView registration(){
    ModelAndView modelAndView = new ModelAndView();
    User user = new User();
    modelAndView.addObject("user", user);
    modelAndView.setViewName("registration");
    return modelAndView;
  }

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult,
   @RequestParam(value = "username", required = true) String email,
   @RequestParam(value = "inputPassword", required = true) String password,
  @RequestParam(value = "confirmPassword", required = true) String confirmPassword) {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("registration");
    User userExists = userService.findUserByEmail(email);
    if (userExists != null) {
      modelAndView.addObject("failure", new Boolean(true));
      bindingResult
      .rejectValue("email", "error.user",
      "There is already a user registered with the email provided");
      modelAndView.setViewName("registration");
    } else if (!password.equals(confirmPassword)) {
      modelAndView.addObject("passwordNotMatch", new Boolean(true));
    } else {
      modelAndView.addObject("success", new Boolean(true));
      User newUser = new User();
      newUser.setPassword(password);
      newUser.setEmail(email);
      userService.saveUser(newUser);
      modelAndView.addObject("successMessage", "User has been registered successfully");
      modelAndView.addObject("user", new User());
    }
    return modelAndView;
  }

}
