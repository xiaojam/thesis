package id.go.kemenag.spn.constant;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AuthConstant {

    public AuthConstant() throws Exception {
        throw new Exception("Utility");
    }

    public enum Role {
        // Role Default untuk user yang belum di set rolenya
        DEFAULT("DEFAULT"),

        // MARRIAGE
        // Petugas Pendaftaran Nikah di Desa
        REGISTRAR("REGISTRAR"),
        // Kepala Desa
        HEADMAN("HEADMAN"),
        // Petugas KUA
        OFFICER("OFFICER"),
        // Kepala KUA
        APPROVER("APPROVER"),

        // DIVORCE
        // Hanya punya satu role
        ADMINISTRATOR("ADMINISTRATOR"),

        USER("USER"),
        SUPER_USER("SUPER_USER");

        private final String label;

        Role(String label) {
            this.label = label;
        }

        private static final Map<String, Role> stringToEnum = Stream
            .of(values())
            .collect(Collectors.toMap(Object::toString, e -> e));

        public static Optional<Role> fromString(String label) {
            return Optional.ofNullable(stringToEnum.get(label.toUpperCase()));
        }
    }

    public static final String ROLE_REGISTRAR = "hasRole('REGISTRAR')";
    public static final String ROLE_HEADMAN = "hasRole('HEADMAN')";
    public static final String ROLE_OFFICER = "hasRole('OFFICER')";
    public static final String ROLE_APPROVER = "hasRole('APPROVER')";
    public static final String ROLE_USER = "hasRole('USER')";
    public static final String ROLE_SUPER_USER = "hasRole('SUPER_USER')";

    public static final String ROLE_VILLAGE = ROLE_REGISTRAR + " or " + ROLE_HEADMAN;
    public static final String ROLE_RELIGIOUS_AFFAIRS = ROLE_OFFICER + " or " + ROLE_APPROVER;
    public static final String ROLE_ADMIN = ROLE_USER + " or " + ROLE_SUPER_USER;

    public  static final String ROLE_MARRIAGE_PROCESSOR = ROLE_VILLAGE + " or " + ROLE_RELIGIOUS_AFFAIRS;

    public static final String ALL_ROLE = ROLE_REGISTRAR + " or " + ROLE_HEADMAN + " or " + ROLE_OFFICER + " or " + ROLE_APPROVER + " or " + ROLE_USER + " or " + ROLE_SUPER_USER;
}
