package id.go.kemenag.spn.constant;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public enum ApprovedStatus {
        APPROVED,
        CANCELLED,
    }

    public enum WorkplaceType {
        VILLAGE("VILLAGE"),
        OFFICE("OFFICE");

        private final String label;

        WorkplaceType(String label) {
            this.label = label;
        }

        private static final Map<String, WorkplaceType> stringToEnum = Stream
            .of(values())
            .collect(Collectors.toMap(Object::toString, e -> e));

        public static Optional<WorkplaceType> fromString(String label) {
            return Optional.ofNullable(stringToEnum.get(label.toUpperCase()));
        }
    }

    public static final String API_KEY_VALID_ATTRIBUTE = "IS_API_KEY_VALID";
}
