package id.go.kemenag.spn.service;

public interface RedisService {

    void save(String key, String value, Long ttl);

    String get(String key);

    void delete(String key);
}
