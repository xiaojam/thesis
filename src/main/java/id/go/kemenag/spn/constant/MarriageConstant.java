package id.go.kemenag.spn.constant;

public class MarriageConstant {

    public MarriageConstant() throws Exception {
        throw new Exception("Utility");
    }

    public enum MaritalStatus {
        DIVORCE,
        MARRIED,
        SINGLE,
        WIDOWED,
    }

    public enum Gender {
        MALE,
        FEMALE
    }

    // Wali Nikah
    public enum GuardianStatus {
        NASAB,
        HAKIM,
    }

    // Alasan Jadi Wali
    public enum GuardianReason {
        DIED,
        UNKNOWN,
        NONE,
    }

    public enum Religion {
        ISLAM,
        CATHOLIC,
        CHRISTIAN,
        BUDDHA,
        HINDU,
        KONG_HU_CHU,
        BELIEVE,
    }

    public enum LocationType {
        BRIDE_HOME,
        GROOM_HOME,
        BRIDE_KUA,
        GROOM_KUA,
        OTHER
    }

    public enum GuardianType {
        FATHER,
        GRANDFATHER,
        BROTHER,
        UNCLE,
        JUDGE,
    }
}
