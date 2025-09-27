package id.go.kemenag.spn.util;

import id.go.kemenag.spn.entity.master.Master;

import java.util.List;

public class MasterUtil {

    public static Master getKUA(List<Master> children) {
        return children.stream()
            .filter(c -> "KUA".equalsIgnoreCase(c.getGroupName()))
            .findFirst()
            .orElse(null);
    }

    public static Master getReligiousCourt(List<Master> children) {
        return children.stream()
            .filter(c -> "PENGADILAN_AGAMA".equalsIgnoreCase(c.getGroupName()))
            .findFirst()
            .orElse(null);
    }
}
