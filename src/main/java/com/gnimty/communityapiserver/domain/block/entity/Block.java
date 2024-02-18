package com.gnimty.communityapiserver.domain.block.entity;

import com.gnimty.communityapiserver.domain.base.entity.BaseEntity;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(
    name = "block",
    indexes = {
        @Index(name = "blocker_id_blocked_id_idx", columnList = "blocker_id, blocked_id")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Block extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "block_id", columnDefinition = "BIGINT", updatable = false, unique = true)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", nullable = false)
    private Member blocker;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", nullable = false)
    private Member blocked;

    @Column(name = "memo", columnDefinition = "VARCHAR(100)")
    private String memo;

    @Builder
    public Block(
        Member blocker,
        Member blocked,
        String memo
    ) {
        this.blocker = blocker;
        this.blocked = blocked;
        this.memo = memo;
    }
}
