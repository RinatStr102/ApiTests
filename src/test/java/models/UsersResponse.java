package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsersResponse {
    private int page;
    private int per_page;
    private int total;
    private int total_pages;
    private List<User> data;

    // Геттеры и сеттеры
}
