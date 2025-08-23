package id.go.kemenag.spn.entity.marriage;

import id.go.kemenag.spn.dto.base.DataDocument;
import id.go.kemenag.spn.entity.base.BaseEntity;
import jakarta.persistence.Column;

import java.util.List;
import java.util.UUID;

public class Document extends BaseEntity {

    @Column
    private Long id;

    @Column
    private String villageCode;

    @Column
    private UUID spouseId;

    @Column
    private List<DataDocument> documents;

}
