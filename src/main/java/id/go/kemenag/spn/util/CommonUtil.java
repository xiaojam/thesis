package id.go.kemenag.spn.util;

import id.go.kemenag.spn.constant.DivorceConstant;
import id.go.kemenag.spn.constant.MarriageConstant;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.UUID;

public class CommonUtil {

    private static final int MAX_LENGTH = 25;

    public static String removeSpecialCharacters(String input) {
        if (input == null) {
            return null;
        }

        return input.replaceAll("[^a-zA-Z0-9]", "");
    }

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

    public static String buildFullAddress(String address, String rt, String rw, String subDistrict, String district, String city, String province, String zipCode) {
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

        if (zipCode != null && !zipCode.trim().isEmpty()) {
            if (!fullAddress.isEmpty()) {
                fullAddress.append(" - ");
            }
            fullAddress.append(zipCode.trim());
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

    public static String normalizeTotalDuration(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            return "";
        }

        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1; // inclusive
        long years = totalDays / 365;
        long months = (totalDays % 365) / 30;
        long days = (totalDays % 365) % 30;

        StringBuilder result = new StringBuilder();
        if (years > 0) {
            result.append(years).append(" tahun ");
        }
        if (months > 0) {
            result.append(months).append(" bulan ");
        }
        if (days > 0) {
            result.append(days).append(" hari");
        }

        return result.toString().trim();
    }

    public static String normalizeDateTime(LocalDateTime date) {
        if (date == null) {
            return "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy/HH:mm", new Locale("id", "ID"));
        return date.format(formatter) + " WIB";
    }

    public static String normalizeZonedDateTime(ZonedDateTime date) {
        if (date == null) {
            return "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy '\n pukul ' HH:mm ' WIB'", new Locale("id", "ID"));
        return date.format(formatter);
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

    public static String simplifyUUID(UUID uuid) {
        if (uuid == null) {
            return "";
        }

        String uuidStr = uuid.toString();
        if (uuidStr.length() <= 10) {
            return uuidStr;
        }

        return uuidStr.substring(0, 5) + uuidStr.substring(uuidStr.length() - 5);
    }

    public static String formatCurrency(Double amount) {
        if (amount == null) return null;

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.of("id", "ID"));
        currencyFormatter.setMinimumFractionDigits(0);
        currencyFormatter.setMaximumFractionDigits(0);

        return currencyFormatter.format(amount).replace(",00", "");
    }

    public static String normalizeDocumentLabel(String label) {

        // example input bride_full_name -> nama lengkap pengantin wanita
        if (label == null || label.isEmpty()) {
            return "";
        }

        return switch (label) {
            case "bride_full_name" -> "Nama Lengkap Pengantin Wanita";
            case "bride_identity_id" -> "Nomor KTP Pengantin Wanita";
            case "bride_birth_info" -> "Tempat dan Tanggal Lahir Pengantin Wanita";
            case "bride_address" -> "Alamat Pengantin Wanita";
            case "groom_full_name" -> "Nama Lengkap Pengantin Pria";
            case "groom_identity_id" -> "Nomor KTP Pengantin Pria";
            case "groom_birth_info" -> "Tempat dan Tanggal Lahir Pengantin Pria";
            case "groom_address" -> "Alamat Pengantin Pria";
            case "marriage_time" -> "Waktu Pernikahan";
            case "marriage_location" -> "Tempat Pernikahan";
            case "marriage_dowry" -> "Mahar Pernikahan";
            case "previous_groom_partner_full_name" -> "Nama Lengkap Mantan Pasangan Suami";
            case "previous_groom_partner_identity_id" -> "Nomor KTP Mantan Pasangan Suami";
            case "previous_groom_partner_birth_info" -> "Tempat dan Tanggal Lahir Mantan Pasangan Suami";
            case "previous_groom_partner_death_info" -> "Tempat dan Tanggal Meninggal Mantan Pasangan Suami";
            case "previous_groom_partner_father_name" -> "Nama Ayah Mantan Pasangan Suami";
            case "previous_groom_partner_address" -> "Alamat Mantan Pasangan Suami";
            case "previous_bride_partner_full_name" -> "Nama Lengkap Mantan Pasangan Istri";
            case "previous_bride_partner_identity_id" -> "Nomor KTP Mantan Pasangan Istri";
            case "previous_bride_partner_birth_info" -> "Tempat dan Tanggal Lahir Mantan Pasangan Istri";
            case "previous_bride_partner_death_info" -> "Tempat dan Tanggal Meninggal Mantan Pasangan Istri";
            case "previous_bride_partner_father_name" -> "Nama Ayah Mantan Pasangan Istri";
            case "previous_bride_partner_address" -> "Alamat Mantan Pasangan Istri";
            case "guardian_full_name" -> "Nama Lengkap Wali";
            case "guardian_identity_id" -> "Nomor KTP Wali";
            case "guardian_birth_info" -> "Tempat dan Tanggal Lahir Wali";
            case "guardian_address" -> "Alamat Wali";
            case "bride_mother_full_name" -> "Nama Lengkap Ibu Pengantin Wanita";
            case "bride_mother_father_name" -> "Nama Kakek Pengantin Wanita";
            case "bride_mother_identity_id" -> "Nomor KTP Ibu Pengantin Wanita";
            case "bride_mother_birth_info" -> "Tempat dan Tanggal Lahir Ibu Pengantin Wanita";
            case "bride_mother_address" -> "Alamat Ibu Pengantin Wanita";
            case "groom_mother_full_name" -> "Nama Lengkap Ibu Pengantin Pria";
            case "groom_mother_father_name" -> "Nama Kakek Pengantin Pria";
            case "groom_mother_identity_id" -> "Nomor KTP Ibu Pengantin Pria";
            case "groom_mother_birth_info" -> "Tempat dan Tanggal Lahir Ibu Pengantin Pria";
            case "groom_mother_address" -> "Alamat Ibu Pengantin Pria";
            case "bride_father_full_name" -> "Nama Lengkap Ayah Pengantin Wanita";
            case "bride_father_father_name" -> "Nama Kakek Pengantin Wanita";
            case "bride_father_identity_id" -> "Nomor KTP Ayah Pengantin Wanita";
            case "bride_father_birth_info" -> "Tempat dan Tanggal Lahir Ayah Pengantin Wanita";
            case "bride_father_address" -> "Alamat Ayah Pengantin Wanita";
            case "groom_father_full_name" -> "Nama Lengkap Ayah Pengantin Pria";
            case "groom_father_father_name" -> "Nama Kakek Pengantin Pria";
            case "groom_father_identity_id" -> "Nomor KTP Ayah Pengantin Pria";
            case "groom_father_birth_info" -> "Tempat dan Tanggal Lahir Ayah Pengantin Pria";
            case "groom_father_address" -> "Alamat Ayah Pengantin Pria";
            default -> CommonUtil.removeSpecialCharacters(label);
        };
    }
}
