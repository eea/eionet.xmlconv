package eionet.gdem;

public interface SchedulingConstants {

    int WORKER_RECEIVED = 0;
    int WORKER_SUCCESS = 1;
    int WORKER_FATAL_ERR = 2;
    int WORKER_INTERRUPTED = 3;

    int INTERNAL_STATUS_RECEIVED = 1;
    int INTERNAL_STATUS_QUEUED = 2;
    int INTERNAL_STATUS_PROCESSING = 3;
}
