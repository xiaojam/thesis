package id.go.kemenag.spn.entity.divorce;

import id.go.kemenag.spn.constant.DivorceConstant;
import id.go.kemenag.spn.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaseSchedule extends BaseEntity {

    @Column
    @Id
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "divorce_case_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private DivorceCase divorceCase;

    @Enumerated(EnumType.STRING)
    @Column
    private DivorceConstant.SetDateType dateType;

    @Column
    private LocalDate eventDate;

    @Column
    private Integer processStep;

    @Column
    private Integer dailyQueueNumber;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column
    private DivorceConstant.ScheduleStatus status = DivorceConstant.ScheduleStatus.SCHEDULED;
}
