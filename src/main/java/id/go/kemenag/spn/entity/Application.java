package id.go.kemenag.spn.entity;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Application extends BaseEntity {

    @Column
    @Id
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    private UUID id;

    @Column
    @Enumerated(EnumType.STRING)
    private ApplicationConstant.Status status;

    @Column
    private UUID processId;

    @Column
    @Enumerated(EnumType.STRING)
    private ApplicationConstant.Type type;
}
