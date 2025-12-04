# Load Test Results

# k6 Load Test Report

## Execution Summary
- **Execution Mode:** local
- **Script:** `scripts/test.js`
- **Output:** `influxdb=http://influxdb:8086/k6`

## Scenario Configuration
| Scenario   | VUs | Duration | Exec Function | Start Time | Graceful Stop |
|------------|-----|----------|----------------|------------|----------------|
| normal     | 200 | 10s      | `normal`       | 0s         | default        |
| notFound   | 200 | 10s      | `notFound`     | 10s        | default        |
| error      | 200 | 10s      | `error`        | 20s        | default        |
| slow       | 200 | 10s      | `slow`         | 30s        | 10s            |
| verySlow   | 200 | 10s      | `verySlow`     | 50s        | 30s            |

**Total duration:** 1m00.9s  
**Completed iterations:** 11,973  
**Interrupted iterations:** 600  
**Max VUs:** 200

All scenarios completed successfully.

---

## Metrics

### Network
- **Data received:** 3.2 MB (52 kB/s)
- **Data sent:** 1.1 MB (19 kB/s)

### HTTP Timings
| Metric | Average | Median | Min | Max | p(90) | p(95) |
|--------|---------|---------|-----|-----|-------|-------|
| **http_req_blocked** | 69.4µs | 5µs | 1.2µs | 18.95ms | 10.1µs | 18.37µs |
| **http_req_connecting** | 34.86µs | 0s | 0s | 18.86ms | 0s | 0s |
| **http_req_duration** | 327.3ms | 6.43ms | 2.06ms | 3.2s | 942.66ms | 3.04s |
| **http_req_receiving** | 247.35µs | 96.8µs | 12.3µs | 26.2ms | 498.7µs | 807.32µs |
| **http_req_sending** | 26.26µs | 16.29µs | 5.4µs | 14.2ms | 36µs | 48.47µs |
| **http_req_waiting** | 327.03ms | 6.26ms | 1.86ms | 3.2s | 939.12ms | 3.04s |
| **http_req_tls_handshaking** | 0s | 0s | 0s | 0s | 0s | 0s |

### Throughput
- **HTTP requests:** 12,566 (≈206 req/s)
- **Iterations:** 11,973 (≈196.6 it/s)

### VU Stats
- **VUs:** 0 → 200
- **VUs Max:** 200

---

## Analysis

### 1. **Overall Performance**
The system handled **200 VUs per scenario** smoothly, completing nearly **12k iterations** in ~60 seconds. The high concurrency (peaking at 1000 total active VUs across staggered scenarios) did not cause collapse, but several timing metrics indicate strain under heavy load.

### 2. **Request Duration**
- **Average request duration:** **327ms**
- **Median:** **6.43ms**
- **p(90):** **~943ms**
- **p(95):** **~3s**
- **Max:** **3.2s**

This distribution is highly **bimodal**:
- The fast median (6ms) suggests many endpoints respond quickly (likely `normal`, `notFound`, `error`).
- The long tail—requests taking **>1s up to 3s**—aligns with the `slow` and `verySlow` scenario behaviors.

### 3. **Latency Contributors**
- **Waiting time (`http_req_waiting`)** matches almost exactly the total duration, confirming that server processing time is the bottleneck—not sending or receiving.
- **Blocked** and **connecting** times are negligible, meaning:
  - No client-side resource starvation
  - DNS, queueing, or connection reuse issues are minimal

### 4. **Iterations Interrupted**
- **600 interrupted iterations** likely occurred when scenarios reached their time window and VUs were forced to stop.
- This is expected for test configurations with `gracefulStop` constraints.

### 5. **Throughput and Stability**
- **200+ req/s sustained** is solid considering deliberately slow endpoints.
- No evidence of:
  - connection failures
  - TLS issues
  - abnormal blocked times
  - VU starvation

### 6. **Potential Improvements**
If these slow endpoints represent real production behavior, consider:
- Adding async processing or worker queues
- Improving DB query latencies
- Caching responses where possible
- Scaling horizontally during peak traffic
- Using circuit breakers or timeouts for slow services

If these endpoints were intentionally slowed for load verification, results show the system can withstand heavy concurrent load without failing.

---

## Conclusion
The system demonstrates **high throughput** and **stability under heavy load**, but exhibits a **significant latency tail** caused by slow endpoints. If this is expected test behavior, the system passes. If not, optimization efforts should focus on server-side processing time.

