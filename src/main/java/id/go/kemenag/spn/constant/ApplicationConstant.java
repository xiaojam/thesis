package id.go.kemenag.spn.constant;

public class ApplicationConstant {

    public ApplicationConstant() throws Exception {
        throw new Exception("Utility");
    }

    public enum Status {
        CREATED,
        PROCESSED,
        CANCELLED,
        TRADITIONAL_PROCESS,
        DONE,
    }

    public enum Type {
        MARRIAGE,
        DIVORCE,
    }

    public static final String API_KEY_VALID_ATTRIBUTE = "IS_API_KEY_VALID";
}
