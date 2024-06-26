package com.fatec.donation.domain.request;

import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.entity.User;
import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Node("JoinRequest")
@Builder
public class JoinGroupRequest {
    @Id
    private UUID id;
    @Relationship(type = "REQUESTED_BY", direction = Relationship.Direction.OUTGOING)
    private User user;
    @Relationship(type = "FOR_GROUP", direction = Relationship.Direction.OUTGOING)
    private Group group;
    private LocalDateTime createdAt;
    public JoinGroupRequest() {
        this.id = UUID.randomUUID();
    }
}
