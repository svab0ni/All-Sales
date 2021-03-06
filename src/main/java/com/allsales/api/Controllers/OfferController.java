package com.allsales.api.Controllers;

import com.allsales.api.Helpers.Slug;
import com.allsales.api.Models.City;
import com.allsales.api.Models.Contract;
import com.allsales.api.Models.Offer;
import com.allsales.api.Repositories.CityRepository;
import com.allsales.api.Repositories.ContractRepository;
import com.allsales.api.Repositories.OfferRepository;
import com.allsales.api.Repositories.UserRepository;
import com.allsales.api.Requests.UpdateOfferRequest;
import com.allsales.api.security.JwtTokenUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/offers")
public class OfferController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private OfferRepository offerRepository;
    private CityRepository cityRepository;
    private ContractRepository contractRepository;
    private UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public OfferController(OfferRepository offerRepository, CityRepository cityRepository, ContractRepository contractRepository, UserRepository userRepository){
        this.offerRepository = offerRepository;
        this.cityRepository = cityRepository;
        this.contractRepository = contractRepository;
        this.userRepository = userRepository;
    }


    @RequestMapping(value = "create", method = RequestMethod.POST)
    public ResponseEntity<Offer> create(@RequestBody Offer offer, HttpServletRequest request){

        String token = request.getHeader(tokenHeader).substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);

        offer.setAlias(Slug.makeSlug(offer.getTitle()));
        offer.setOfferUser(userRepository.findByUsername(username));
        offerRepository.save(offer);

        return new ResponseEntity<>(offer, HttpStatus.OK);
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    public HttpStatus destroy(@PathVariable("id") Long id){

        offerRepository.deleteById(id);

        return HttpStatus.OK;
    }

    @RequestMapping(value = "index", method = RequestMethod.GET)
    public ResponseEntity<List<Offer>> index(){

        List<Offer> offers = offerRepository.findAllByOrderByIdDesc();

        return new ResponseEntity<>(offers, HttpStatus.OK);
    }

    @RequestMapping(value = "find/{id}", method = RequestMethod.GET)
    public ResponseEntity<Offer> find(@PathVariable Long id){

        Offer offer = offerRepository.findOfferById(id);

        return new ResponseEntity<>(offer, HttpStatus.OK);
    }

    @RequestMapping(value = "update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Offer> update(@PathVariable("id") Long id, @RequestBody Offer offer){
        Offer newOffer = offerRepository.findOfferById(id);
//        City city = cityRepository.findCityById(offer.getCityId());
//        Contract contract = contractRepository.findContractById(offer.getContractId());

        newOffer.setSubtitle(offer.getSubtitle());
        newOffer.setTitle(offer.getTitle());
        newOffer.setShortDescription(offer.getShortDescription());
        newOffer.setOfferCity(offer.getOfferCity());
        newOffer.setOfferContract(offer.getOfferContract());
        newOffer.setPublished(offer.getPublished());
        newOffer.setProcessed(offer.getProcessed());
        newOffer.setImageUrl(offer.getImageUrl());
        newOffer.setName(offer.getName());
        newOffer.setPreviousPrice(offer.getPreviousPrice());
        newOffer.setCurrentPrice(offer.getCurrentPrice());
        newOffer.setAlias(Slug.makeSlug(offer.getTitle()));

        offerRepository.save(newOffer);

        return new ResponseEntity<>(newOffer, HttpStatus.OK);
    }

    @RequestMapping(value = "search/{q}", method = RequestMethod.GET)
    public ResponseEntity<List<Offer>> search(@PathVariable String q){

        List<Offer> offers = offerRepository.findByTitleIgnoreCaseContainingOrderByIdDesc(q);

        return new ResponseEntity<>(offers, HttpStatus.OK);
    }
}
