package id.go.kemenag.spn.entity.divorce;

import id.go.kemenag.spn.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class MarriageData extends BaseEntity {

    @Column
    @Id
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    private UUID id;

    @Column
    private LocalDate marriageDate;

    @Column
    private String marriagePlace;

    @Column
    private String marriageCertificateNumber;

    @Column
    private String householdAddress;

    @Column
    private Boolean hasChildren;
}
