package id.go.kemenag.spn.service.impl;

import id.go.kemenag.spn.repository.HolidayRepository;
import id.go.kemenag.spn.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
public class HolidayServiceImpl implements HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;


    @Override
    public Set<LocalDate> findHolidays(Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        return this.holidayRepository.findActiveHolidayDatesByYear(year);
    }
}
