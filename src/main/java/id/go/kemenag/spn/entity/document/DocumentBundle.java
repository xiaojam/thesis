package id.go.kemenag.spn.entity.document;

import id.go.kemenag.spn.constant.DocumentConstant;
import id.go.kemenag.spn.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentBundle extends BaseEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    private UUID id;

    @Column
    @Enumerated(EnumType.STRING)
    private DocumentConstant.ServiceType serviceType;

    @Column
    private UUID referenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_id")
    private DocumentConfig config;

    @Column
    private String number;

    @Column
    private LocalDateTime issuedAt;
}
