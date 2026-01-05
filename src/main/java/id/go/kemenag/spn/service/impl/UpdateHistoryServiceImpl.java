package id.go.kemenag.spn.service.impl;

import id.go.kemenag.spn.entity.UpdateHistory;
import id.go.kemenag.spn.repository.UpdateHistoryRepository;
import id.go.kemenag.spn.service.UpdateHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UpdateHistoryServiceImpl implements UpdateHistoryService {

    @Autowired
    private UpdateHistoryRepository updateHistoryRepository;

    @Override
    public List<UpdateHistory> findAllByApplicationId(UUID applicationId) {
        return Streamable
            .of(this.updateHistoryRepository.findByApplicationIdAndDeletedIsFalse(applicationId))
            .stream()
            .toList();
    }

    @Override
    public List<UpdateHistory> saveAll(List<UpdateHistory> updateHistories) {
        return Streamable.of(this.updateHistoryRepository.saveAll(updateHistories)).stream().toList();
    }
}
