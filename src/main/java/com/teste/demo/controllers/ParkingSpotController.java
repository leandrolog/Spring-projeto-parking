package com.teste.demo.controllers;

import com.teste.demo.dtos.ParkingSpotDto;
import com.teste.demo.models.ParkingSpotModel;
import com.teste.demo.services.ParkingSpotService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {

  final ParkingSpotService parkingSpotService;

  public ParkingSpotController(ParkingSpotService parkingSpotRepository) {
    this.parkingSpotService = parkingSpotRepository;
  }
  @PostMapping
  public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto parkingSpotDto){
    if(parkingSpotService.existsByLicensePlateCar(parkingSpotDto.getLicensePlateCar())){
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: license Plate Car iS already in Use!");
    }
    if(parkingSpotService.existsByParkingSpotNumber(parkingSpotDto.getParkingSpotNumber())){
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: parking Spot is already in use!");
    }
    if(parkingSpotService.existsByApartmentAndBlock(parkingSpotDto.getApartment(), parkingSpotDto.getBlock())){
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: parking Spot aldeady registered for this partment/block!");
    }
    var parkingSpotModel = new ParkingSpotModel();
    BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
    parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
    return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
  }

  @GetMapping
  public ResponseEntity<List<ParkingSpotModel>> getAllParkingSpots(){
    return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value = "id") UUID id){
    Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
    if (!parkingSpotModelOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
    }
    return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteParkingSpot(@PathVariable(value = "id") UUID id){
    Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
    if (!parkingSpotModelOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
    }
    parkingSpotService.delete(parkingSpotModelOptional.get());
    return ResponseEntity.status(HttpStatus.OK).body("Parking Spot deleted successfully.");
  }
}
