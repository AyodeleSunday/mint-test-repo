package mint.server.service;

import mint.server.exception.MintException;
import mint.server.exception.NotFoundException;
import mint.server.model.CardSchemePayload;
import mint.server.model.CardSchemeResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class CardService {
    @Value("${back.service.url}")
    private String baseUrl;
    RestTemplate restTemplate;
    @PostConstruct
    private void init(){
        HttpComponentsClientHttpRequestFactory rf = new HttpComponentsClientHttpRequestFactory();
        restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(rf));
    }

    public CardSchemeResponse getCardScheme(String bin){
        CardSchemeResponse response=new CardSchemeResponse();
        String fullUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(bin)
                .build().toUriString();
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity httpEntity=new HttpEntity(headers);
        ResponseEntity<String> responseEntity=restTemplate.exchange(fullUrl, HttpMethod.GET, httpEntity, String.class);
        if(responseEntity.getStatusCode()==HttpStatus.OK){
            JSONObject json= new JSONObject(responseEntity.getBody());
            response.setSuccess("true");
            CardSchemePayload payload=new CardSchemePayload();
            payload.setBank(json.getJSONObject("bank").getString("name"));
            payload.setScheme(json.getString("scheme"));
            payload.setType(json.getString("type"));
            response.setPayload(payload);
            return response;
        }
        else if(responseEntity.getStatusCode()==HttpStatus.NOT_FOUND){
            throw new NotFoundException(responseEntity.getBody());
        }
        else throw new MintException(responseEntity.getBody());
    }
}
