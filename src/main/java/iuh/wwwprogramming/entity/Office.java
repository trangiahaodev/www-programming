package iuh.wwwprogramming.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "offices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Office {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "working_hours", length = 100)
    private String workingHours;

    @Column(name = "support_type", length = 100)
    private String supportType;

    @Column(name = "coupon", length = 50)
    private String coupon;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "office_perks", joinColumns = @JoinColumn(name = "office_id"))
    @Column(name = "perk", length = 255)
    private List<String> perks;

    @Column(name = "image", length = 500)
    private String image;

    @Column(name = "active")
    @Builder.Default
    private boolean active = true;
}
