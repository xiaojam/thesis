package id.go.kemenag.spn.service;

import java.time.LocalDate;
import java.util.Set;

public interface HolidayService {

    Set<LocalDate> findHolidays(Integer year);
}
