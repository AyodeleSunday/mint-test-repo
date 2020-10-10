package mint.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import mint.server.exception.MintException;
import mint.server.exception.NotFoundException;
import mint.server.model.BinHit;
import mint.server.model.CardSchemePayload;
import mint.server.model.CardSchemeResponse;
import mint.server.model.StatResponse;
import mint.server.repository.CardRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class CardService {
    Logger logger= LoggerFactory.getLogger(getClass());

    @Value("${back.service.url}")
    private String baseUrl;
    @Value("${mint.kafka.topic}")
    private String kafkaTopic;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    RestTemplate restTemplate;
    @PostConstruct
    private void init(){
        HttpComponentsClientHttpRequestFactory rf = new HttpComponentsClientHttpRequestFactory();
        restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(rf));
    }

    public CardSchemeResponse getCardScheme(String bin){
        this.updateBinHit(bin);
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
            CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(() -> sendMessage(payload));
            return response;
        }
        else if(responseEntity.getStatusCode()==HttpStatus.NOT_FOUND){
            throw new NotFoundException(responseEntity.getBody());
        }
        else throw new MintException(responseEntity.getBody());
    }
    public void updateBinHit(String bin){
        BinHit binHit=null;
        if(cardRepository.existsById(bin)){
            binHit=cardRepository.findById(bin).get();
            binHit.setHitCount(binHit.getHitCount()+1);
        }
        else{
            binHit=new BinHit();
            binHit.setBin(bin);
            binHit.setHitCount(1);
        }
        cardRepository.save(binHit);
    }
    public StatResponse getHits(int page, int size){
        Pageable pageable=PageRequest.of(page,size, Sort.by("bin").descending());
        Page<BinHit> pageRsp= cardRepository.findAll(pageable);
        StatResponse response=new StatResponse();
        response.setSuccess(true);
        response.setLimit(size);
        response.setStart(page);
        response.setSize(pageRsp.getTotalElements());
        Map<String,Integer> payload=pageRsp.getContent().stream().collect(Collectors.toMap(BinHit::getBin,BinHit::getHitCount));
        response.setPayload(payload);
        return response;
    }
    public boolean sendMessage(CardSchemePayload payload) {

        try{
            String msg=new ObjectMapper().writeValueAsString(payload);
            kafkaTemplate.send(kafkaTopic,msg);
            logger.info("Message sent");
            return true;
        }
        catch (Exception ex){
            logger.error("Error",ex);
            return false;
        }
    }
}
