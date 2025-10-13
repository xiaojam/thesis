package id.go.kemenag.spn.repository;

import id.go.kemenag.spn.entity.Holiday;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Repository
public interface HolidayRepository extends CrudRepository<Holiday, UUID> {

    @Query("SELECT h.holidayDate FROM Holiday h WHERE h.deleted = false AND EXTRACT(YEAR FROM h.holidayDate) = :year")
    Set<LocalDate> findActiveHolidayDatesByYear(@Param("year") Integer year);
}
