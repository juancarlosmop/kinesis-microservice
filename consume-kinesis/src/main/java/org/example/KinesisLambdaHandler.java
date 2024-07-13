package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.dto.UserDTO;
import org.example.service.BusinessService;
import org.example.util.UnixTimestampDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;

@Component
public class KinesisLambdaHandler implements RequestStreamHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final ConfigurableApplicationContext applicationContext;

    static {
        applicationContext = new SpringApplicationBuilder(Application.class)
                .run();
    }

    @Autowired
    private BusinessService businessService;

    public KinesisLambdaHandler() {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        CustomKinesisEvent customEvent = objectMapper.readValue(inputStream, CustomKinesisEvent.class);
        for (CustomKinesisRecord record : customEvent.getRecords()) {
            String data = new String(record.getKinesis().getData().array());
            context.getLogger().log("Data from Kinesis record: " + data);
            UserDTO userDTO = getObject(data);
            businessService.validAge(userDTO);
            // Agregar lógica adicional según sea necesario
        }
    }

    private UserDTO getObject(String json) {
        try {
            return objectMapper.readValue(json, UserDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize JSON to UserDTO", e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CustomKinesisEvent {
        @JsonProperty("Records")
        private List<CustomKinesisRecord> records;

        public List<CustomKinesisRecord> getRecords() {
            return records;
        }

        public void setRecords(List<CustomKinesisRecord> records) {
            this.records = records;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CustomKinesisRecord {
        @JsonProperty("kinesis")
        private CustomKinesisData kinesis;
        @JsonDeserialize(using = UnixTimestampDeserializer.class)
        private Date approximateArrivalTimestamp;

        public CustomKinesisData getKinesis() {
            return kinesis;
        }

        public void setKinesis(CustomKinesisData kinesis) {
            this.kinesis = kinesis;
        }

        public Date getApproximateArrivalTimestamp() {
            return approximateArrivalTimestamp;
        }

        public void setApproximateArrivalTimestamp(Date approximateArrivalTimestamp) {
            this.approximateArrivalTimestamp = approximateArrivalTimestamp;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CustomKinesisData {
        @JsonProperty("data")
        private ByteBuffer data;

        public ByteBuffer getData() {
            return data;
        }

        public void setData(ByteBuffer data) {
            this.data = data;
        }
    }
}

