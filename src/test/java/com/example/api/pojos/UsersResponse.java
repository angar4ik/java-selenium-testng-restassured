package com.example.api.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsersResponse {

    private int page;

    @JsonProperty("per_page")
    private int perPage;

    private int total;

    @JsonProperty("total_pages")
    private int totalPages;

    private java.util.List<User> data;

    private Support support;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Support {
        private String url;
        private String text;
    }
}
