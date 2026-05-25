package com.wellu.community.follow.graph;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;

@Node("User")
public class UserNode {

    @Id
    private UUID id;

    public UserNode() {
    }

    public UserNode(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
