package id.go.kemenag.spn.entity.marriage;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @Column
    @Id
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    private UUID id;

    @Column
    @NotBlank
    private String username;

    @Column
    @Enumerated(EnumType.STRING)
    private ApplicationConstant.Role role;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    @NotBlank
    private String password;

    @Column
    private String workplaceCode;

    @Column
    private String workplaceName;
}
