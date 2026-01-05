package id.go.kemenag.spn.entity.divorce;

import id.go.kemenag.spn.constant.DivorceConstant;
import id.go.kemenag.spn.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Defendant extends BaseEntity {

    @Column
    @Id
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    private UUID id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String alias;

    @Column
    private String birthPlace;

    @Column
    private LocalDate birthDate;

    @Column
    private Integer age;

    @Column
    @Enumerated(EnumType.STRING)
    private DivorceConstant.Religion religion;

    @Column
    @Enumerated(EnumType.STRING)
    private DivorceConstant.Gender gender;

    @Column
    private String nationality;

    @Column
    @Enumerated(EnumType.STRING)
    private DivorceConstant.IdentityType identityType;

    @Column
    private String identityNumber;

    @Column
    private String fatherName;

    @Column
    @Enumerated(EnumType.STRING)
    private DivorceConstant.MaritalStatus maritalStatus;

    @Column
    private String education;

    @Column
    private String job;

    @Column
    private Double salary;

    @Column
    private String phoneNumber;

    @Column
    private String provinceCode;

    @Column
    private String provinceName;

    @Column
    private String cityCode;

    @Column
    private String cityName;

    @Column
    private String districtCode;

    @Column
    private String districtName;

    @Column
    private String subDistrictCode;

    @Column
    private String subDistrictName;

    @Column
    private String address;

    @Column
    @Size(min = 3, max = 3)
    private String rw;

    @Column
    @Size(min = 3, max = 3)
    private String rt;

    @Column
    private String zipCode;
}
