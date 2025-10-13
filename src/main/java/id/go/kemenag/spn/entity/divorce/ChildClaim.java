package id.go.kemenag.spn.entity.divorce;

import id.go.kemenag.spn.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChildClaim extends BaseEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "divorce_case_id")
    private DivorceCase divorceCase;

    @ManyToMany
    @JoinTable(
        name = "child_claim_relation",
        joinColumns = @JoinColumn(name = "child_claim_id"),
        inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    private List<Child> children;

    @Column(name = "custody_request")
    private String custodyRequest;

    @Column(name = "monthly_support")
    private Double monthlySupport;
}
