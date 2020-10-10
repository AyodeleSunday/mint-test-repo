package mint.server.web;

import mint.server.model.Response;
import mint.server.model.StatResponse;
import mint.server.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = {"/card-scheme"})
public class CardController {

    @Autowired
    CardService cardService;

    @RequestMapping(path = "/verify/{bin}", method = RequestMethod.GET, produces = "application/json")
    public Response verifyBin(@PathVariable @Valid String bin) {
        Response response;
        response = cardService.getCardScheme(bin);
        return response;
    }
    @RequestMapping(path = "/stats", method = RequestMethod.GET, produces = "application/json")
    public StatResponse getBinHit(@RequestParam int start, @RequestParam int limit) {
        StatResponse response = cardService.getHits(start, limit);
        return response;
    }
}
