package com.gnimty.communityapiserver.domain.introduction.entity;

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
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "introduction",
	indexes = {
		@Index(name = "member_id_is_main_idx", columnList = "member_id, is_main")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Introduction extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "introduction_id", columnDefinition = "BIGINT", updatable = false, unique = true)
	private Long id;

	@Column(name = "content", columnDefinition = "VARCHAR(90)")
	private String content;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@NotNull
	@Column(name = "is_main", columnDefinition = "TINYINT")
	private Boolean isMain;

	@Builder
	public Introduction(
		String content,
		Member member,
		Boolean isMain
	) {
		this.content = content;
		this.member = member;
		this.isMain = isMain;
	}

	public void updateContent(String content) {
		this.content = content;
	}

	public void updateIsMain(Boolean isMain) {
		this.isMain = isMain;
	}
}
