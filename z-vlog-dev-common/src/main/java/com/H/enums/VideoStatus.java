package com.H.enums;

public enum VideoStatus {
	SUCCESS(1),		//发布成功
	FORBID(2);		//禁止播放，由管理员操作
	public final int value;
	VideoStatus(int value){
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}
