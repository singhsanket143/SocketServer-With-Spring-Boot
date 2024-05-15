package com.example.ubersocketserver.controller;


import com.example.ubersocketserver.dto.RideRequestDto;
import com.example.ubersocketserver.dto.RideResponseDto;
import com.example.ubersocketserver.dto.UpdateBookingRequestDto;
import com.example.ubersocketserver.dto.UpdateBookingResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
@RequestMapping("/api/socket")
public class DriverRequestController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RestTemplate restTemplate;


    public DriverRequestController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping
    public Boolean help() {
        return true;
    }

    @PostMapping ("/newride")
    @CrossOrigin(originPatterns = "*")
    public ResponseEntity<Boolean> raiseRideRequest(@RequestBody RideRequestDto requestDto) {
        System.out.println("request for rides received");
        sendDriversNewRideRequest(requestDto);
        System.out.println("Req completed");
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    public void sendDriversNewRideRequest(RideRequestDto requestDto) {
        System.out.println("Executed periodic function");
        // TODO: Ideally the request should only go to nearby drivers, but for simplicity we send it everyone
        simpMessagingTemplate.convertAndSend("/topic/rideRequest", requestDto);
    }

    @MessageMapping("/rideResponse/{userId}")
    public synchronized void rideResponseHandler(@DestinationVariable String userId, RideResponseDto rideResponseDto) {

        System.out.println(rideResponseDto.getResponse() +" "+userId);
        UpdateBookingRequestDto requestDto = UpdateBookingRequestDto.builder()
                .driverId(Optional.of(Long.parseLong(userId)))
                .status("SCHEDULED")
                .build();
        ResponseEntity<UpdateBookingResponseDto> result = this.restTemplate.postForEntity("http://localhost:8001/api/v1/booking/" + rideResponseDto.bookingId, requestDto, UpdateBookingResponseDto.class);
        System.out.println(result.getStatusCode());
    }
}
