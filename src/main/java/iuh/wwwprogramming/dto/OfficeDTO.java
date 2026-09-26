package iuh.wwwprogramming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfficeDTO {
    private String id;
    private String title;
    private String address;
    private String description;
    private String city;
    private String phone;
    private String email;
    private String workingHours;
    private String supportType;
    private String coupon;
    private List<String> perks;
    private String image;
}
