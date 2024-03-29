package com.teamE.rooms;

import com.teamE.ads.data.entity.Ad;
import com.teamE.common.UsersDemandingController;
import com.teamE.common.ValidationHandler;
import com.teamE.imageDestinations.Destination;
import com.teamE.imageDestinations.ImageDestination;
import com.teamE.imageDestinations.ImageDestinationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController()
@RequestMapping("rooms")
public class RoomController extends UsersDemandingController {
    private RoomRepository roomRepository;
    private RoomWithConfigurationProjectionProcessor roomWithConfigurationProjectionProcessor;
    private RoomPOJOToRoomTransformer roomPOJOToRoomTransformer;
    private RoomPOJOValidator roomPOJOValidator;
    private ImageDestinationRepo imageDestinationRepo;
    private RoomSearch roomSearch;

    @Autowired
    public RoomController(RoomRepository roomRepository, RoomWithConfigurationProjectionProcessor roomWithConfigurationProjectionProcessor, RoomPOJOToRoomTransformer roomPOJOToRoomTransformer, RoomPOJOValidator roomPOJOValidator, ImageDestinationRepo imageDestinationRepo, RoomSearch roomSearch) {
        super();
        this.roomRepository = roomRepository;
        this.roomWithConfigurationProjectionProcessor = roomWithConfigurationProjectionProcessor;
        this.roomPOJOToRoomTransformer = roomPOJOToRoomTransformer;
        this.roomPOJOValidator = roomPOJOValidator;
        this.imageDestinationRepo = imageDestinationRepo;
        this.roomSearch = roomSearch;
    }

    @PostMapping()
    public Room save(@RequestBody @Validated RoomPOJO pojo) {
        Room room = roomPOJOToRoomTransformer.transform(pojo);

        Room savedRoom = roomRepository.save(room);

        Long mainImage = savedRoom.getMainImage();

        if (mainImage != null) {
            Optional<ImageDestination> temp = imageDestinationRepo.findById(mainImage);
            if (temp.isPresent()) {
                ImageDestination imageDestination = temp.get();
                imageDestination.setIdDestination(savedRoom.getId());
                imageDestination.setDestination(Destination.ROOM);
                imageDestinationRepo.save(imageDestination);
            }
        }

        return savedRoom;

    }

    public Page<EntityModel<RoomWithConfigurationDto>> findForUser(final Pageable pageable, final String query) {
        Page<RoomWithConfigurationDto> page = roomSearch.search(query, getUserStudentHouse(), pageable);
        return page.map(e -> roomWithConfigurationProjectionProcessor.process(e));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        return ValidationHandler.handleValidationExceptions(ex);
    }

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(roomPOJOValidator);
    }
}
