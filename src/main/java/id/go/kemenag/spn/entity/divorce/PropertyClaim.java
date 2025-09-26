package id.go.kemenag.spn.entity.divorce;

import id.go.kemenag.spn.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyClaim extends BaseEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "divorce_case_id")
    private DivorceCase divorceCase;

    @Column(name = "division_request", columnDefinition = "TEXT")
    private String divisionRequest;

    @OneToMany(mappedBy = "propertyClaim", cascade = CascadeType.ALL)
    private List<SharedProperty> properties;
}
