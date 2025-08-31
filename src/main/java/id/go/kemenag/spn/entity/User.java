package id.go.kemenag.spn.entity;

import id.go.kemenag.spn.constant.AuthConstant;
import id.go.kemenag.spn.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "\"user\"")
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
    private AuthConstant.Role role;

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
