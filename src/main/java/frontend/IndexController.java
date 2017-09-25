package frontend;

import org.springframework.stereotype.Controller;

@Controller
public class IndexController {

    public String index() {
        return "index";
    }

}
