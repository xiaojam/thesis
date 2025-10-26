package id.go.kemenag.spn.util;

import id.go.kemenag.spn.constant.DivorceConstant;
import id.go.kemenag.spn.constant.MarriageConstant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.StringJoiner;

public class CommonUtil {

    private static final int MAX_LENGTH = 25;

    public static String buildFullName(String firstName, String lastName, String alias) {
        StringBuilder fullName = new StringBuilder();

        String fName = (firstName != null) ? firstName.trim() : null;
        if (fName != null && !fName.isEmpty()) {
            fullName.append(fName);
        }

        String lName = (lastName != null) ? lastName.trim() : null;
        if (lName != null && !lName.isEmpty()) {
            if (!fullName.isEmpty()) {
                fullName.append(" ");
            }
            fullName.append(lName);
        }

        String al = (alias != null) ? alias.trim() : null;
        if (al != null && !al.isEmpty()) {
            fullName.append("/");
            fullName.append(al);
        }

        return fullName.toString();
    }

    public static String buildFullAddress(String address, String rt, String rw, String subDistrict, String district, String city, String province) {
        StringBuilder fullAddress = new StringBuilder();

        if (address != null && !address.trim().isEmpty()) {
            fullAddress.append(address.trim());
        }

        if (rt != null && !rt.trim().isEmpty()) {
            if (!fullAddress.isEmpty()) {
                fullAddress.append(", ");
            }
            fullAddress.append("RT ").append(rt.trim());
        }

        if (rw != null && !rw.trim().isEmpty()) {
            if (!fullAddress.isEmpty()) {
                fullAddress.append(", ");
            }
            fullAddress.append("RW ").append(rw.trim());
        }

        if (subDistrict != null && !subDistrict.trim().isEmpty()) {
            if (!fullAddress.isEmpty()) {
                fullAddress.append(", ");
            }
            fullAddress.append(subDistrict.trim());
        }

        if (district != null && !district.trim().isEmpty()) {
            if (!fullAddress.isEmpty()) {
                fullAddress.append(", ");
            }
            fullAddress.append(district.trim());
        }

        if (city != null && !city.trim().isEmpty()) {
            if (!fullAddress.isEmpty()) {
                fullAddress.append(", ");
            }
            fullAddress.append(city.trim());
        }

        if (province != null && !province.trim().isEmpty()) {
            if (!fullAddress.isEmpty()) {
                fullAddress.append(", ");
            }
            fullAddress.append(province.trim());
        }

        return fullAddress.toString();
    }

    public static String buildBirthInfo(String birthPlace, LocalDate birthDate) {
        StringBuilder birthInfo = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));

        if (birthPlace != null && !birthPlace.trim().isEmpty()) {
            birthInfo.append(birthPlace.trim());
        }

        if (birthDate != null) {
            if (!birthInfo.isEmpty()) {
                birthInfo.append(", ");
            }
            birthInfo.append(birthDate.format(formatter));
        }

        return birthInfo.toString();
    }

    public static String buildSignatureInfo(String location, LocalDate date) {
        StringBuilder signatureInfo = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));

        if (location != null && !location.trim().isEmpty()) {
            signatureInfo.append(location.trim());
        }

        if (date != null) {
            if (!signatureInfo.isEmpty()) {
                signatureInfo.append(", ");
            }
            signatureInfo.append(date.format(formatter));
        }

        return signatureInfo.toString();
    }

    public static String normalizeDate(LocalDate date) {
        if (date == null) {
            return "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));
        return date.format(formatter);
    }

    public static String normalizeDateTime(LocalDateTime date) {
        if (date == null) {
            return "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy/HH:mm", new Locale("id", "ID"));
        return date.format(formatter) + " WIB";
    }

    public static String getRomanMonth(int month) {
        String[] roman = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        if (month < 1 || month > 12) return "ERR";
        return roman[month - 1];
    }

    public static String normalizeGender(MarriageConstant.Gender gender) {
        if (gender == null) {
            return "";
        }

        return switch (gender) {
            case MALE -> "Laki-laki";
            case FEMALE -> "Perempuan";
        };
    }

    public static String normalizeGender(DivorceConstant.Gender gender) {
        if (gender == null) {
            return "";
        }

        return switch (gender) {
            case MALE -> "Laki-laki";
            case FEMALE -> "Perempuan";
        };
    }

    public static String normalizeMaritalStatus(MarriageConstant.MaritalStatus status, Boolean isMale) {
        if (status == null) {
            return "";
        }

        return switch (status) {
            case SINGLE -> isMale ? "Jejaka" : "Perawan";
            case MARRIED -> "Kawin";
            case DIVORCE -> isMale ? "Duda (Cerai Hidup)" : "Janda (Cerai Hidup)";
            case WIDOWED -> isMale ? "Duda (Cerai Mati)" : "Janda (Cerai Mati)";
        };
    }

    public static String buildFullNameWithFatherName(String fullName, String fatherName, Boolean isMale) {
        StringBuilder result = new StringBuilder();

        if (fullName != null && !fullName.trim().isEmpty()) {
            result.append(fullName.trim());
        }

        if (fatherName != null && !fatherName.trim().isEmpty()) {
            if (!result.isEmpty()) {
                result.append(" ");
            }
            result.append(isMale != null && isMale ? "bin " : "binti ");
            result.append(fatherName.trim());
        }

        return result.toString();
    }

    public static String normalizeReligion(MarriageConstant.Religion religion) {
        if (religion == null) {
            return "";
        }

        return switch (religion) {
            case ISLAM -> "Islam";
            case CHRISTIAN -> "Protestan";
            case CATHOLIC -> "Katolik";
            case HINDU -> "Hindu";
            case BUDDHA -> "Buddha";
            case KONG_HU_CHU -> "Konghucu";
            case BELIEVE -> "Kepercayaan";
        };
    }


    public static String normalizeReligion(DivorceConstant.Religion religion) {
        if (religion == null) {
            return "";
        }

        return switch (religion) {
            case ISLAM -> "Islam";
            case CHRISTIAN -> "Protestan";
            case CATHOLIC -> "Katolik";
            case HINDU -> "Hindu";
            case BUDDHA -> "Buddha";
            case KONG_HU_CHU -> "Konghucu";
            case BELIEVE -> "Kepercayaan";
        };
    }

    public static String simplifiedName(String firstName, String lastName) {
        String fn = (firstName != null) ? firstName.trim() : "";
        String ln = (lastName != null) ? lastName.trim() : "";

        if (fn.isEmpty() && ln.isEmpty()) {
            return "";
        }

        if (fn.isEmpty()) {
            return (ln.length() <= MAX_LENGTH) ? ln : simplifyLastNameOnly(ln);
        }

        if (ln.isEmpty()) {
            return fn;
        }

        String combinedFullName = fn + " " + ln;
        if (combinedFullName.length() <= MAX_LENGTH) {
            return combinedFullName;
        }

        String[] lastNameParts = ln.split("\\s+");
        StringBuilder result = new StringBuilder(fn);

        if (lastNameParts.length > 0 && !lastNameParts[0].isEmpty()) {
            result.append(" ").append(lastNameParts[0]);

            StringJoiner initials = new StringJoiner(". ", " ", "."); // Adds " X. Y. Z."
            boolean addedInitials = false;
            for (int i = 1; i < lastNameParts.length; i++) {
                if (!lastNameParts[i].isEmpty()) {
                    initials.add(String.valueOf(lastNameParts[i].charAt(0)));
                    addedInitials = true;
                }
            }
            if (addedInitials) {

                result.append(initials.toString());
                if (result.length() > MAX_LENGTH) {
                    result = new StringBuilder(fn);
                    result.append(" ").append(lastNameParts[0].charAt(0)).append(".");
                    result.append(initials.toString());
                }
            }

            else if (result.length() > MAX_LENGTH) {
                result = new StringBuilder();
                result.append(fn.charAt(0)).append(". ").append(lastNameParts[0]);
                if (result.length() > MAX_LENGTH) {
                    result = new StringBuilder();
                    result.append(fn.charAt(0)).append(". ").append(lastNameParts[0].charAt(0)).append(".");
                }
            }

        } else {
            return fn;
        }

        if (result.length() > MAX_LENGTH) {
            if (!ln.isEmpty()) {
                return fn.charAt(0) + ". " + ln.charAt(0) + ".";
            } else {
                return fn.substring(0, Math.min(fn.length(), MAX_LENGTH));
            }
        }

        return result.toString();
    }

    public static String simplifyLastNameOnly(String lastName) {
        if (lastName.length() <= MAX_LENGTH) return lastName;

        String[] parts = lastName.split("\\s+");
        if (parts.length <= 1) {
            return lastName.substring(0, MAX_LENGTH - 3) + "...";
        }

        StringBuilder result = new StringBuilder(parts[0]);
        StringJoiner initials = new StringJoiner(". ", " ", ".");
        boolean addedInitials = false;
        for(int i = 1; i < parts.length; i++){
            if(!parts[i].isEmpty()){
                initials.add(String.valueOf(parts[i].charAt(0)));
                addedInitials = true;
            }
        }
        if(addedInitials) result.append(initials.toString());

        if(result.length() > MAX_LENGTH){
            result = new StringBuilder();
            StringJoiner allInitials = new StringJoiner(". ", "", ".");
            for (String part : parts) {
                if (!part.isEmpty()) {
                    allInitials.add(String.valueOf(part.charAt(0)));
                }
            }
            result.append(allInitials.toString());
        }

        if (result.length() > MAX_LENGTH) {
            return result.substring(0, MAX_LENGTH);
        }
        return result.toString();
    }

    public static String normalizeGuardianStatus(MarriageConstant.GuardianStatus status) {
        if (status == null) {
            return "";
        }

        return switch (status) {
            case NASAB -> "Nasab";
            case HAKIM -> "Hakim";
        };
    }

    public static String getNormalizeReason(MarriageConstant.GuardianStatus status, Boolean isFatherDeceased) {
        if (status == null) {
            return "";
        }

        return switch (status) {
            case NASAB -> isFatherDeceased != null && isFatherDeceased ?
                "Ayah kandung telah meninggal dunia" :
                "............................................................................";
            case HAKIM -> "Sudah tidak ada nasab yang dapat menjadi wali";
        };
    }

    public static String getNormalizeGuardianType(MarriageConstant.GuardianType type) {
        if (type == null) {
            return "";
        }

        return switch (type) {
            case FATHER -> "Ayah";
            case GRANDFATHER -> "Kakek";
            case BROTHER -> "Saudara laki-laki";
            case UNCLE -> "Paman";
            case JUDGE -> "Hakim";
        };
    }
}
