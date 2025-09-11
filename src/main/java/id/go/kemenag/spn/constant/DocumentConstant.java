package id.go.kemenag.spn.constant;

public class DocumentConstant {

    public DocumentConstant() throws Exception {
        throw new Exception("Utility");
    }

    public enum DocumentType {
        N1, // Pengantar Nikah
        N2, // Permohonan Kehendak Nikah
        N4, // Persetujuan Calon Pengantin
        N5, // Izin Orang Tua
        N6, // Keterangan Kematian
        N7, // Penolakan Kehendak Nikah
        N8, // Pemeriksaan Nikah
        WN, // Keterangan Wali Nikah
    }

    public enum ServiceType {
        MARRIAGE,
        DIVORCE,
    }
}
