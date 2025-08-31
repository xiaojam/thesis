package id.go.kemenag.spn.service.impl;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.entity.Application;
import id.go.kemenag.spn.repository.ApplicationRepository;
import id.go.kemenag.spn.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public List<Application> findAllAndStatusInAndType(
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

}
