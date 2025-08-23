package id.go.kemenag.spn.entity;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.entity.base.BaseEntity;
import id.go.kemenag.spn.entity.marriage.Bride;
import id.go.kemenag.spn.entity.marriage.Groom;
import id.go.kemenag.spn.entity.marriage.Marriage;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

//@Data
//@Builder
//@EqualsAndHashCode(callSuper = true)
//@Entity
public class Application extends BaseEntity {

    @Column
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    private UUID id;

    @Column
    @Enumerated(EnumType.STRING)
    private ApplicationConstant.Status status;

    @Column
    private String processId;

    @Column
    @Enumerated(EnumType.STRING)
    private ApplicationConstant.Type type;

    @Column
    private String applicationId;
}
