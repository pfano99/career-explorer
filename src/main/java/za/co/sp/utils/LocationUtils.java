package za.co.sp.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationUtils {


    public static final Map<String, String> CITY_TO_PROVINCE;
    public static final List<String> PROVINCES = List.of(
            "Limpopo",
            "Mpumalanga",
            "Gauteng",
            "Eastern Cape",
            "Free State",
            "KwaZulu Natal",
            "Western Cape",
            "North West",
            "Northern Cape"
    );

    @Data
    private static class City {
        @JsonProperty("City")
        private String city;
        @JsonProperty("AccentCity")
        private String accentCity;
        @JsonProperty("ProvinceName")
        private String provinceName;
        @JsonProperty("Latitude")
        private double latitude;
        @JsonProperty("Longitude")
        private double Longitude;
        @JsonProperty("ProvinceID")
        private int provinceID;
    }

    static {

        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = new ClassPathResource("data/SouthAfricanCities.json").getInputStream()) {
            // Read JSON array into List<City>
            List<City> cities = objectMapper.readValue(inputStream, new TypeReference<List<City>>() {
            });
            cities.forEach(city -> {
                city.city = city.city.toLowerCase();
            });
            // Convert List to Map<City, ProvinceName>
            CITY_TO_PROVINCE = cities.stream()
                    .collect(Collectors.toMap(City::getCity, City::getProvinceName, (_, replacement) -> replacement));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
