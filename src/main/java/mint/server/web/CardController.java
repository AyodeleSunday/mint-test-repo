package mint.server.web;

import mint.server.model.Response;
import mint.server.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/card-scheme")
public class CardController {

    @Autowired
    CardService cardService;

    @RequestMapping(value = "/verify", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response debitCardNewAccount(@RequestParam @Valid String bin) {
        Response response;
        response = cardService.getCardScheme(bin);
        return response;
    }
}
