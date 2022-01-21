package eionet.gdem;

public interface SchedulingConstants {

    int WORKER_RECEIVED = 0;
    int WORKER_READY = 1;
    int WORKER_FAILED = 2;
    int WORKER_RECEIVED_FME_JOB_ID = 12;

    int INTERNAL_STATUS_RECEIVED = 1;
    int INTERNAL_STATUS_QUEUED = 2;
    int INTERNAL_STATUS_PROCESSING = 3;
    int INTERNAL_STATUS_CANCELLED = 4;

    public enum CONTAINER_STATE_ENUM {

        RUNNING("running"),
        STOPPING("stopping"),
        STOPPED("stopped"),
        ERROR("error");

        private String value;

        CONTAINER_STATE_ENUM(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum CONTAINER_HEALTH_STATE_ENUM {
        HEALTHY("healthy"), UNHEALTHY("unhealthy"), UPDATING_HEALTHY("updating-healthy"),
        UPDATING_UNHEALTHY("updating-unhealthy"), INITIALIZING("initializing");
        private String value;

        /**
         * Constructor
         * @param value value
         */
        CONTAINER_HEALTH_STATE_ENUM(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    };
}
