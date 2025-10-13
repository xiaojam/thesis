package id.go.kemenag.spn.entity.divorce;

import id.go.kemenag.spn.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SharedProperty extends BaseEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "property_claim_id")
    private PropertyClaim propertyClaim;

    @Column(name = "property_type")
    private String propertyType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "estimated_value")
    private Double estimatedValue;

    @Column(name = "ownership_proof")
    private String ownershipProof;
}
