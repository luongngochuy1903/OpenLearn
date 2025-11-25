package com.example.online.model;

import com.example.online.enumerate.CommunityRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "community_members")
public class CommunityMember {

    @ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Float starContribute;

    @Enumerated(EnumType.STRING)
    private CommunityRole role;
}
