package id.go.kemenag.spn.entity.divorce;

import id.go.kemenag.spn.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
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

    @Column(name = "conflict_start_date")
    private LocalDate conflictStartDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "divorce_conflict_causes", joinColumns = @JoinColumn(name = "reason_id"))
    @Column(name = "cause")
    private List<String> conflictCauses;

    @Column(name = "conflict_climax_date")
    private LocalDate conflictClimaxDate;

    @Column(name = "separation_date")
    private LocalDate separationDate;
}
