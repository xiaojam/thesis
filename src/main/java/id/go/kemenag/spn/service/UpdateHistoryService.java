package id.go.kemenag.spn.service;

import id.go.kemenag.spn.entity.UpdateHistory;

import java.util.List;
import java.util.UUID;

public interface UpdateHistoryService {

    List<UpdateHistory> findAllByApplicationId(UUID applicationId);

    List<UpdateHistory> saveAll(List<UpdateHistory> updateHistories);
}
