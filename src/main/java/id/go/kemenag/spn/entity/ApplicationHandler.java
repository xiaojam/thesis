package id.go.kemenag.spn.entity;

import id.go.kemenag.spn.constant.AuthConstant;
import id.go.kemenag.spn.entity.base.BaseEntity;
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
public class ApplicationHandler extends BaseEntity {

    @Column
    @Id
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", referencedColumnName = "id")
    private Application application;

    @Column
    private String username;

    @Column
    @Enumerated(EnumType.STRING)
    private AuthConstant.Role role;

    @Column
    private String workplaceCode;
}
