package com.example.produce.controller;

import com.example.produce.dto.UserDTO;
import com.example.produce.service.KinesisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class KinesisProducerController {
    @Autowired
    private KinesisService kinesisService;

    @PostMapping("/produce")
    public CompletableFuture<String> produceToKinesis(@RequestParam String partitionKey, @RequestBody UserDTO user) {
        return kinesisService.putRecord(partitionKey, user);
    }
}
