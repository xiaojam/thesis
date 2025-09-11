package id.go.kemenag.spn.service.impl;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.constant.AuthConstant;
import id.go.kemenag.spn.entity.Application;
import id.go.kemenag.spn.repository.ApplicationRepository;
import id.go.kemenag.spn.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public List<Application> findAllByStatusInAndType(
        List<ApplicationConstant.Status> statusList,
        ApplicationConstant.Type type
    ) {
        return Streamable
            .of(
                this.applicationRepository.findAllByDeletedFalseAndStatusInAndTypeOrderByCreatedAtAsc(
                    statusList,
                    type
                )
            )
            .stream()
            .toList();
    }

    @Override
    public Application save(Application application) {
        return this.applicationRepository.save(application);
    }

    @Override
    public List<UUID> collectIds(List<Application> applications) {
        return applications.stream().map(Application::getId).collect(Collectors.toList());
    }

    @Override
    public void dropExpiredApplications() {
        ZonedDateTime expiredAt = ZonedDateTime.now().minusDays(30);
        List<Application> applications = Streamable
            .of(
                this.applicationRepository.findAllByStatusAndCreatedAtBeforeAndDeletedFalse(
                    ApplicationConstant.Status.CREATED,
                    expiredAt
                )
            )
            .stream()
            .toList();

        if (applications.isEmpty()) {
            log.info("No expired applications found");
            return;
        }

        applications.forEach(application -> {
            application.setStatus(ApplicationConstant.Status.CANCELLED);
            application.setDeleted(Boolean.TRUE);
            application.setDeletedAt(ZonedDateTime.now());
            this.applicationRepository.save(application);
        });
    }

    @Override
    public Application findById(UUID id) {
        return this.applicationRepository.findByIdAndDeletedFalse(id).orElse(null);
    }

    @Override
    public List<Application> findAllABasedOnHandler(
        List<ApplicationConstant.Status> statusList,
        ApplicationConstant.Type type,
        AuthConstant.Role handlerRole,
        String handlerWorkplaceCode
    ) {
        return Streamable
            .of(
                this.applicationRepository.findAllByHandler(
                    statusList,
                    type,
                    handlerRole,
                    handlerWorkplaceCode
                )
            )
            .stream()
            .toList();
    }

    @Override
    public Application findByIdBasedOnHandler(
        UUID id,
        AuthConstant.Role handlerRole,
        String handlerWorkplaceCode
    ) {
        return this.applicationRepository.findOneByHandler(id, handlerRole, handlerWorkplaceCode).orElse(null);
    }
}
