package com.teamE.ads.controllers;

import com.teamE.ads.data.AdsRepo;
import com.teamE.ads.data.entity.*;
import com.teamE.common.UsersDemandingController;
import com.teamE.common.ValidationHandler;
import com.teamE.commonAddsEvents.Scope;
import com.teamE.commonAddsEvents.converters.ScopeConverter;
import com.teamE.commonAddsEvents.converters.StudentHouseConverter;
import com.teamE.imageDestinations.Destination;
import com.teamE.imageDestinations.ImageDestination;
import com.teamE.imageDestinations.ImageDestinationRepo;
import com.teamE.users.StudentHouse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/ads")
public class AdsController extends UsersDemandingController {

    private ImageDestinationRepo imageDestinationRepo;
    private AdValidator adValidator;
    private AdPOJOToAdTransformer adPOJOToAdTransformer;
    private AdsRepo adsRepo;
    private AdProcessor adProcessor;

    @Autowired
    public AdsController(ImageDestinationRepo imageDestinationRepo, AdValidator adValidator, AdPOJOToAdTransformer adPOJOToAdTransformer, AdsRepo adsRepo, AdProcessor adProcessor) {
        this.imageDestinationRepo = imageDestinationRepo;
        this.adValidator = adValidator;
        this.adPOJOToAdTransformer = adPOJOToAdTransformer;
        this.adsRepo = adsRepo;
        this.adProcessor = adProcessor;
    }

    public Page<EntityModel<Ad>> findForUser(final Pageable pageable, final String query) {
        //FIXME dodać scope
        Page<Ad> page = adsRepo.findAllByScopeAndStudentHouseAndQuery(null, getUserStudentHouse(), query, pageable);
        return page.map(e -> adProcessor.process(e));
    }

    @PostMapping
    public Ad addAd(@RequestBody @Validated AdPOJO adPojo) {
        Ad ad = adPOJOToAdTransformer.transform(adPojo);
        Ad savedAd = adsRepo.save(ad);

        Long mainImage = ad.getMainImage();
        List<Long> additionalImages = adPojo.getAdditionalImages();

        Optional<ImageDestination> temp;

        if (mainImage != null) {
            temp = imageDestinationRepo.findById(mainImage);
            if (temp.isPresent()) {
                ImageDestination imageDestination = temp.get();
                imageDestination.setIdDestination(savedAd.getId());
                imageDestination.setDestination(Destination.AD);
                imageDestinationRepo.save(imageDestination);
            }
        }

        if (CollectionUtils.isNotEmpty(additionalImages)) {
            for (Long image : additionalImages) {
                temp = imageDestinationRepo.findById(image);
                if (temp.isPresent()) {
                    ImageDestination imageDestination = temp.get();
                    imageDestination.setIdDestination(savedAd.getId());
                    imageDestination.setDestination(Destination.AD);
                    imageDestinationRepo.save(imageDestination);
                }
            }
        }
        return savedAd;
    }

    @InitBinder
    public void initBinder(final WebDataBinder webdataBinder) {
        webdataBinder.registerCustomEditor(Scope.class, new ScopeConverter());
        webdataBinder.registerCustomEditor(StudentHouse.class, new StudentHouseConverter());
        webdataBinder.setValidator(adValidator);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        return ValidationHandler.handleValidationExceptions(ex);
    }
}
