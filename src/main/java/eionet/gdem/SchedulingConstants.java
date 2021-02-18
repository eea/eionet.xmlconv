package eionet.gdem;

public interface SchedulingConstants {

    int WORKER_RECEIVED = 0;
    int WORKER_READY = 1;
    int WORKER_FAILED = 2;

    int INTERNAL_STATUS_RECEIVED = 1;
    int INTERNAL_STATUS_QUEUED = 2;
    int INTERNAL_STATUS_PROCESSING = 3;
}
