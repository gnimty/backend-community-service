package com.gnimty.communityapiserver.domain.memberlike.entity;

import com.gnimty.communityapiserver.domain.base.entity.BaseEntity;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "member_like",
	indexes = {
		@Index(name = "source_member_id_target_member_id_idx", columnList = "source_member_id, target_member_id")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberLike extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_like_id", columnDefinition = "BIGINT", updatable = false, unique = true)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "source_member_id", nullable = false)
	private Member sourceMember;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "target_member_id", nullable = false)
	private Member targetMember;

	@Builder
	public MemberLike(
		Member sourceMember,
		Member targetMember
	) {
		this.sourceMember = sourceMember;
		this.targetMember = targetMember;
	}
}
