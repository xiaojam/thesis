package id.go.kemenag.spn.constant;

public class DocumentConstant {

    public DocumentConstant() throws Exception {
        throw new Exception("Utility");
    }

    public enum DocumentType {
        // Pengantar Nikah
        N1_BRIDE,
        N1_GROOM,

        // Permohonan Kehendak Nikah
        N2_BRIDE,
        N2_GROOM,

        // Persetujuan Calon Pengantin
        N4_BRIDE,
        N4_GROOM,

        // Izin Orang Tua
        N5_BRIDE,
        N5_GROOM,

        // Keterangan Kematian
        N6_BRIDE,
        N6_GROOM,

        // Keterangan Wali Nikah
        WN,

        // HISTORY
        UPDATE_HISTORY,

        // Divorce Documents
        PROPERTY,
        CHILD_CUSTODY,
        COMPLETE,
        BASIC,


        NONE,
    }

    public enum ServiceType {
        MARRIAGE,
        DIVORCE,
    }

    public enum BundleMarriageType {
        BRIDE_ONLY,
        GROOM_ONLY,
        COMPLETE,
    }
}
