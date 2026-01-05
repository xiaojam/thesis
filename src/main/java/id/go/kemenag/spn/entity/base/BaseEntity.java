package id.go.kemenag.spn.entity.base;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -7552580381887275336L;

    @Version
    @Column
    private int version;

    @CreationTimestamp
    @Column(updatable = false)
    private ZonedDateTime createdAt;

    @Column(updatable = false, insertable = false)
    private String createdBy;

    @Column
    private boolean deleted = false;

    @Column
    private ZonedDateTime deletedAt;

    @UpdateTimestamp
    @Column
    private ZonedDateTime updatedAt;

    @Column
    private String updatedBy;
}
