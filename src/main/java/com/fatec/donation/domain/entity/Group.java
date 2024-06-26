package com.fatec.donation.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Node("Group")
@Builder
public class Group {
    @Id
    private UUID id;
    private String name;
    private String groupname;
    private String description;
    private String address;
    private LocalDateTime createdAt;
    @Relationship(type = "OWNER", direction = Relationship.Direction.OUTGOING)
    private User owner;
    @Relationship(type = "MEMBER", direction = Relationship.Direction.INCOMING)
    private Set<User> member;
    @Relationship(type = "BLOCKED", direction = Relationship.Direction.INCOMING)
    private Set<User> blocked;
    public Group() {
        this.id = UUID.randomUUID();
    }
}
