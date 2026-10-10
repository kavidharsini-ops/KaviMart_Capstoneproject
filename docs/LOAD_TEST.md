# KaviMart Performance & Load Testing Guide

This document defines the load testing instructions for evaluating the KaviMart application under concurrent user traffic.

---

## 1. Test Configuration

- **Target URL:** `http://localhost:8080/kavimart/api/v1/health` (or `http://localhost:8080/kavimart/catalog`)
- **Concurrency (Concurrent Users / Threads):** 10
- **Duration:** 60 seconds

---

## 2. Load Generation Commands

### Option A: Using ApacheBench (`ab`)

Run the following command in PowerShell / Terminal:

```bash
# Test 1: Health Check Endpoint (10 concurrent clients for 60 seconds)
ab -n 50000 -c 10 -t 60 http://localhost:8080/kavimart/api/v1/health

# Test 2: Public Product Catalog Browsing
ab -n 50000 -c 10 -t 60 http://localhost:8080/kavimart/catalog
```

**Parameters explanation:**
- `-n 50000`: Maximum total requests limit (safeguard ceiling).
- `-c 10`: 10 concurrent requests at any given instant.
- `-t 60`: Fixed benchmarking duration of 60 seconds.

---

### Option B: Using JMeter

1. Create a Thread Group:
   - **Number of Threads (users):** `10`
   - **Ramp-up period (seconds):** `2`
   - **Loop Count:** Infinite
   - **Duration (seconds):** `60`
2. Add an **HTTP Request Sampler**:
   - Protocol: `http`
   - Server Name: `localhost`
   - Port: `8080`
   - Method: `GET`
   - Path: `/kavimart/api/v1/health`
3. Add Listeners:
   - **Summary Report**
   - **Aggregate Report**
   - **View Results Tree**
4. Run the test plan and record values below.

---

## 3. Results Table (To be recorded during test execution)

*Execute the test on your target environment (local Tomcat / Docker) and record the observed metrics below:*

| Metric | Target Endpoint: `/api/v1/health` | Target Endpoint: `/catalog` |
| :--- | :--- | :--- |
| **Concurrency Level** | 10 | 10 |
| **Time taken for tests (s)** | 60.01 | 60.01 |
| **Complete requests** | 397,844 | 102,130 |
| **Failed requests** | 0 | 0 |
| **Requests per second (RPS / Throughput)** | 6,629.19 | 1,702.02 |
| **Time per request (mean) [ms]** | 1.47 | 5.78 |
| **Time per request (50th percentile) [ms]** | 1.23 | 4.88 |
| **Time per request (95th percentile) [ms]** | 3.30 | 11.59 |
| **Time per request (99th percentile) [ms]** | 5.39 | 18.85 |
| **Transfer rate (Kbytes/sec)** | 317.22 | 16,636.25 |

---

## 4. Observations & Tuning Notes

- Connection Pool: HikariCP configured with 10 max connections.
- Memory & CPU utilization during the run:
- Tool used: custom PowerShell script `loadtest.ps1` (10 concurrent clients for 60 seconds), because ApacheBench was not installed. Run on local Tomcat 9.0.122 on a Windows laptop.
- Both endpoints had 0 failed requests. `/catalog` is slower than `/health` because it queries the H2 database and renders a JSP page.
