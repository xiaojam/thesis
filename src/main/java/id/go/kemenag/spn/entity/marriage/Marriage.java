package id.go.kemenag.spn.entity.marriage;

import id.go.kemenag.spn.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Marriage extends BaseEntity {

    @Column
    @Id
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    private UUID id;

    @Column
    private LocalDateTime datetime;

    // Mahar
    @Column
    private String dowry;

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
