package id.go.kemenag.spn.entity.document;

import id.go.kemenag.spn.constant.DocumentConstant;
import id.go.kemenag.spn.entity.base.BaseEntity;
import id.go.kemenag.spn.entity.master.Master;
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
public class DocumentConfig extends BaseEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workplace_id")
    private Master village;

    @Column
    private String headName;

    @Column
    private String numberingFormat;

    @Column
    private Integer lastSequence;

    @Column
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private DocumentConstant.ServiceType serviceType;
}
