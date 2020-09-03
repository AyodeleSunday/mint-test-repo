package mint.server.web;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/card-scheme")
public class CardController {


    @RequestMapping(value = "/verify", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response debitCardNewAccount(@RequestBody @Valid DebitCardNewAccountRequest request) {
        Response response;
        response = cardService.debitCardNewAccount(request);
        return response;
    }
}
