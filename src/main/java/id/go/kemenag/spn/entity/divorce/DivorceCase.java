package id.go.kemenag.spn.entity.divorce;

import id.go.kemenag.spn.constant.DivorceConstant;
import id.go.kemenag.spn.entity.Application;
import id.go.kemenag.spn.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

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
}
