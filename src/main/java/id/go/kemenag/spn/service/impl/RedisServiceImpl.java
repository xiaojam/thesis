package id.go.kemenag.spn.service.impl;

import id.go.kemenag.spn.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void save(String key, String value, Long ttl) {
        this.redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttl));
    }

    @Override
    public String get(String key) {
        return this.redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {
        this.redisTemplate.delete(key);
    }
}
