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
public class DivorceReason extends BaseEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "divorce_case_id")
    private DivorceCase divorceCase;

    @Column(name = "initial_situation", columnDefinition = "TEXT")
    private String initialSituation;

    @Column(name = "conflict_reason", columnDefinition = "TEXT")
    private String conflictReason;

    @Column(name = "reconciliation_attempt", columnDefinition = "TEXT")
    private String reconciliationAttempt;

    @Column(name = "current_condition", columnDefinition = "TEXT")
    private String currentCondition;
}
