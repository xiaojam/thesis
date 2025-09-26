package id.go.kemenag.spn.entity.divorce;

import id.go.kemenag.spn.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Child extends BaseEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "gender")
    private String gender;

    @Column
    private Integer age;

    @Column
    private Boolean inCustodyOfPlaintiff;
}
