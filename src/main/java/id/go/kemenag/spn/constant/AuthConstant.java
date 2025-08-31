package id.go.kemenag.spn.constant;

public class AuthConstant {
    public AuthConstant() throws Exception {
        throw new Exception("Utility");
    }


    public enum Role {
        // Petugas Pendaftaran Nikah di Desa
        REGISTRAR,
        // Kepala Desa
        HEADMAN,
        // Petugas KUA
        OFFICER,
        // Kepala KUA
        APPROVER,
        USER,
        SUPER_USER,
    }

    public static final String ROLE_REGISTRAR = "hasRole('REGISTRAR')";
    public static final String ROLE_HEADMAN = "hasRole('HEADMAN')";
    public static final String ROLE_OFFICER = "hasRole('OFFICER')";
    public static final String ROLE_APPROVER = "hasRole('APPROVER')";
    public static final String ROLE_USER = "hasRole('USER')";
    public static final String ROLE_SUPER_USER = "hasRole('SUPER_USER')";

    public static final String ROLE_VILLAGE = ROLE_REGISTRAR + " or " + ROLE_HEADMAN;
    public static final String ROLE_RELIGIOUS_AFFAIRS = ROLE_OFFICER + " or " + ROLE_APPROVER;
    public static final String ROLE_SUPPORT = ROLE_VILLAGE + " or " + ROLE_RELIGIOUS_AFFAIRS;

    public  static final String ROLE_MARRIAGE_PROCESSOR = ROLE_REGISTRAR + " or " + ROLE_OFFICER;

    public static final String ALL_ROLE = ROLE_REGISTRAR + " or " + ROLE_HEADMAN + " or " + ROLE_OFFICER + " or " + ROLE_APPROVER + " or " + ROLE_USER + " or " + ROLE_SUPER_USER;
}
