package com.gnimty.communityapiserver.global.constant;

import lombok.Getter;

@Getter
public enum Bound {

	CHILD_COMMENTS_DEPTH(1),
	PARENT_COMMENTS_DEPTH(0),
	INITIAL_COUNT(0),
	MAIN_CONTENT_COUNT(1),
	RANDOM_CODE_LENGTH(6),
	MAX_INTRODUCTION_COUNT(3),
	MAIN_INTRODUCTION_COUNT(1),
	;

	public static final int MAX_MEMO_SIZE = 100;
	public static final int MIN_DEPTH_SIZE = 0;
	public static final int MAX_DEPTH_SIZE = 1;
	public static final int MAX_CONTENTS_SIZE = 1000;
	public static final int MAX_INTRODUCTION_CONTENT_SIZE = 90;
	public static final int MIN_HOUR = 0;
	public static final int MAX_HOUR = 24;
	public static final int MAX_REPORT_COMMENT_SIZE = 1000;

	private final int value;

	Bound(int value) {
		this.value = value;
	}
}
