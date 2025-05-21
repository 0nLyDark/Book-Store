package com.dangphuoctai.BookStore.service.impl;

import org.springframework.data.redis.core.RedisTemplate;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import com.dangphuoctai.BookStore.service.BaseRedisService;

import lombok.RequiredArgsConstructor;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class BaseRedisServiceImpl<K, F, V> implements BaseRedisService<K, F, V> {
    @Autowired
    private RedisTemplate<K, V> redisTemplate;

    @Autowired
    private HashOperations<K, F, V> hashOps;

    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    @Override
    public void set(K key, V value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setTimeToLive(K key, long timeout, TimeUnit timeUnit) {
        redisTemplate.expire(key, timeout, timeUnit);
    }

    @Override
    public void setTimeToLiveOnce(K key, long timeout, TimeUnit unit) {
        String markerKey = key.toString() + ":ttl_marker";
        Boolean markerSet = stringRedisTemplate.opsForValue().setIfAbsent(markerKey, "1", timeout, unit);
        if (Boolean.TRUE.equals(markerSet)) {
            redisTemplate.expire(key, timeout, unit);
        }
    }

    @Override
    public boolean tryLock(String lockKey, long timeout, TimeUnit unit) {
        Duration duration = Duration.ofMillis(unit.toMillis(timeout));
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", duration);
        return Boolean.TRUE.equals(success);
    }

    @Override
    public void hashSet(K key, F field, V value) {
        hashOps.put(key, field, value);
    }

    @Override
    public boolean hashExists(K key, F field) {
        return hashOps.hasKey(key, field);
    }

    @Override
    public V get(K key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Map<F, V> getField(K key) {
        return hashOps.entries(key);
    }

    @Override
    public V hashGet(K key, F field) {
        return hashOps.get(key, field);
    }

    @Override
    public List<V> hashGetByFieldPrefix(K key, F fieldPrefix) {
        Map<F, V> entries = hashOps.entries(key);
        return entries.entrySet().stream()
                .filter(e -> e.getKey().toString().startsWith(fieldPrefix.toString()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public List<V> hashGetByField(K key, List<F> fieldNames) {
        return hashOps.multiGet(key, fieldNames)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Set<F> getFieldPrefixes(K key) {
        return hashOps.keys(key);
    }

    @Override
    public void delete(K key) {
        redisTemplate.delete(key);
    }

    @Override
    public void delete(K key, F field) {
        hashOps.delete(key, field);
    }

    @Override
    public void delete(K key, List<F> fields) {
        for (F field : fields) {
            hashOps.delete(key, field);
        }
    }

    @Override
    public String generateOrderCodeWithRedis() {
        String prefix = "ORD";
        // Thêm giờ phút giây vào để mã ngắn gọn và chính xác hơn
        String dateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String redisKey = "order_seq:" + new SimpleDateFormat("yyyyMMdd").format(new Date());

        Long sequence = stringRedisTemplate.opsForValue().increment(redisKey, 1);

        if (sequence == 1) {
            stringRedisTemplate.expire(redisKey, Duration.ofMinutes(10));
        }

        // Mã đơn hàng: ORD + yyyyMMddHHmmss + sequence 3 chữ số
        return prefix + dateTime + String.format("%03d", sequence);
    }
}
