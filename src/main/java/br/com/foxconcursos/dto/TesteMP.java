package br.com.foxconcursos.dto;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TesteMP {

    @JsonProperty("action")
    private String action;

    @JsonProperty("api_version")
    private String apiVersion;

    @JsonProperty("data")
    private Data data;

    @JsonProperty("date_created")
    private String dateCreated;

    @JsonProperty("id")
    private String id;

    @JsonProperty("live_mode")
    private boolean liveMode;

    @JsonProperty("type")
    private String type;

    @JsonProperty("user_id")
    private int userId;

    // Getters and Setters
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isLiveMode() {
        return liveMode;
    }

    public void setLiveMode(boolean liveMode) {
        this.liveMode = liveMode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Inner class to represent the "data" field
    public static class Data {
        @JsonProperty("id")
        private String id;

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    // Main method to test serialization and deserialization
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        // Example JSON string
        String jsonString = "{\n" +
                "  \"action\": \"payment.update\",\n" +
                "  \"api_version\": \"v1\",\n" +
                "  \"data\": {\"id\":\"123456\"},\n" +
                "  \"date_created\": \"2021-11-01T02:02:02Z\",\n" +
                "  \"id\": \"123456\",\n" +
                "  \"live_mode\": false,\n" +
                "  \"type\": \"payment\",\n" +
                "  \"user_id\": 104350301\n" +
                "}";

        try {
            // Deserialize JSON to Java object
            TesteMP paymentUpdate = objectMapper.readValue(jsonString, TesteMP.class);

            // Print the Java object
            System.out.println("Deserialized Java object:");
            System.out.println(paymentUpdate);

            // Serialize Java object to JSON
            String serializedJson = objectMapper.writeValueAsString(paymentUpdate);

            // Print the JSON string
            System.out.println("Serialized JSON string:");
            System.out.println(serializedJson);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "PaymentUpdate{" +
                "action='" + action + '\'' +
                ", apiVersion='" + apiVersion + '\'' +
                ", data=" + data +
                ", dateCreated='" + dateCreated + '\'' +
                ", id='" + id + '\'' +
                ", liveMode=" + liveMode +
                ", type='" + type + '\'' +
                ", userId=" + userId +
                '}';
    }
}
