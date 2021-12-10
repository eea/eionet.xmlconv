package eionet.gdem.rabbitMQ.enums;

public enum RabbitMQPriority {

    OTHER(0),
    ON_DEMAND_UI(2),
    ON_DEMAND_API(3);

    private Integer id;

    public Integer getId() {
        return id;
    }

    RabbitMQPriority(Integer id) {
        this.id = id;
    }
}
