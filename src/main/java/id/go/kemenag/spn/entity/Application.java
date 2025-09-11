package id.go.kemenag.spn.entity;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
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

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ApplicationHandler> applicationHandler = new ArrayList<>();
}
