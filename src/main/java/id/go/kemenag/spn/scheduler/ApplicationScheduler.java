package id.go.kemenag.spn.scheduler;

import id.go.kemenag.spn.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ApplicationScheduler {

    @Autowired
    private ApplicationService applicationService;

    @Scheduled(cron = "@daily")
    public void processExpiredApplications() {
        this.applicationService.dropExpiredApplications();
    }

    @Scheduled(cron = "@daily")
    public void rescheduleLateProcess() {
        this.applicationService.dropExpiredApplications();
    }
}
