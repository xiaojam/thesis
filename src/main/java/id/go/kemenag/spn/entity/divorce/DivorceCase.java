package id.go.kemenag.spn.entity.divorce;

import id.go.kemenag.spn.constant.DivorceConstant;
import id.go.kemenag.spn.entity.Application;
import id.go.kemenag.spn.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DivorceCase extends BaseEntity {

    @Column
    @Id
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", referencedColumnName = "id")
    private Application application;

    @Column
    @Enumerated(EnumType.STRING)
    private DivorceConstant.CaseType caseType;

    @Column
    private String caseNumber;

    @Column
    private String courtCode;

    @Column
    private String courtName;

    @ManyToOne
    @JoinColumn(name = "plaintiff_id")
    private Plaintiff plaintiff;

    @ManyToOne
    @JoinColumn(name = "defendant_id")
    private Defendant defendant;

    @OneToOne
    @JoinColumn(name = "marriage_data_id")
    private MarriageData marriageData;

    @OneToOne(mappedBy = "divorceCase", cascade = CascadeType.ALL)
    private DivorceReason divorceReason;

    @OneToOne(mappedBy = "divorceCase", cascade = CascadeType.ALL)
    private PropertyClaim propertyClaim;

    @OneToOne(mappedBy = "divorceCase", cascade = CascadeType.ALL)
    private ChildClaim childClaim;

    @OneToMany(
        mappedBy = "divorceCase",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<CaseSchedule> schedules = new HashSet<>();
}
