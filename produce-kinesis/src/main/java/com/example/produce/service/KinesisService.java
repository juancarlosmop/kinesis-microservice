package com.example.produce.service;

import com.example.produce.dto.UserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordResponse;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

@Service
public class KinesisService {
    @Autowired
    private KinesisAsyncClient kinesisAsyncClient;

    @Value("${aws.kinesis.streamName}")
    private String streamName;

    public CompletableFuture<String> putRecord(String partitionKey, UserDTO user) {
        String jsonData;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            jsonData = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }

        ByteBuffer dataBytes = ByteBuffer.wrap(jsonData.getBytes());

        PutRecordRequest request = PutRecordRequest.builder()
                .streamName(streamName)
                .partitionKey(partitionKey)
                .data(SdkBytes.fromByteBuffer(dataBytes))
                .build();

        return kinesisAsyncClient.putRecord(request)
                .thenApply(PutRecordResponse::shardId);
    }
}
