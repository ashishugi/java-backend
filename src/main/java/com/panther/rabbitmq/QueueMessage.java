package com.panther.rabbitmq;

import jakarta.persistence.*;

@Entity
@Table(
        name = "test_container_queue"

)
public class QueueMessage  {

    @Id
    @SequenceGenerator(
            name = "test_container_queue_id_seq",
            sequenceName = "test_container_queue_id_seq",
            initialValue = 1,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "test_container_queue_id_seq"
    )
    private Integer id;
    private String title;
    private String description;

    public QueueMessage() {
    }

    public QueueMessage(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public QueueMessage(String title, String description, int id) {
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
