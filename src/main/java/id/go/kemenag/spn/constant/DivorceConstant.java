package id.go.kemenag.spn.constant;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DivorceConstant {

    public DivorceConstant() throws Exception {
        throw new Exception("Utility");
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

    public enum Gender {
        MALE,
        FEMALE
    }

    public enum MaritalStatus {
        DIVORCE,
        MARRIED,
        SINGLE,
        WIDOWED,
    }

    public enum CaseType {
        BASIC,
        PROPERTY,
        CHILD_CUSTODY,
        COMPLETE,
    }

    public enum IdentityType {
        PERSONAL_ID,
        BIRTH_CERTIFICATE,
        FAMILY_CARD,
        PASSPORT,
        DRIVER_LICENSE,
        RESIDENCE_PERMIT,
        OTHER,
    }

    public enum SetDateType {
        COUNCIL_DATE("COUNCIL_DATE"),
        RECONCILIATION_DATE("RECONCILIATION_DATE"),
        DEFENDANT_RESPONSE_DATE("DEFENDANT_RESPONSE_DATE"),
        PLAINTIFF_REPLY_DATE("PLAINTIFF_REPLY_DATE"),
        DEFENDANT_REJOINDER_DATE("DEFENDANT_REJOINDER_DATE"),
        PLAINTIFF_EVIDENCE_DATE("PLAINTIFF_EVIDENCE_DATE"),
        DEFENDANT_EVIDENCE_DATE("DEFENDANT_EVIDENCE_DATE"),
        CLOSING_STATEMENT_DATE("CLOSING_STATEMENT_DATE"),
        VERDICT_DATE("VERDICT_DATE"),

        PLAINTIFF_CASE_AND_EVIDENCE_DATE("PLAINTIFF_CASE_AND_EVIDENCE_DATE");

        private final String label;

        SetDateType(String label) {
            this.label = label;
        }

        private static final Map<String, SetDateType> stringToEnum = Stream
            .of(values())
            .collect(Collectors.toMap(Object::toString, e -> e));

        public static Optional<SetDateType> fromString(String label) {
            return Optional.ofNullable(stringToEnum.get(label.toUpperCase()));
        }
    }

    public enum CouncilResult {
        FAIL,
        DROP,
        HALF,
        FULL,
    }

    public enum ScheduleStatus {
        SCHEDULED,
        COMPLETED,
        RESCHEDULED,
        CANCELLED,
    }
}
