package id.go.kemenag.spn.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class BaseHierarchyData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column
    private String villageCode;

    @Column
    private String villageName;

    @Column
    private String districtCode;

    @Column
    private String districtName;

    @Column
    private String religionAffairsOfficeCode;

    @Column
    private String religionAffairsOfficeName;

}
