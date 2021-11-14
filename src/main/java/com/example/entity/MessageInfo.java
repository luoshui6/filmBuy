package com.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;

@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
@Table(name = "message_info")
public class MessageInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "name")
	private String name;
	@Column(name = "content")
	private String content;
	@Column(name = "time")
	private String time;
	@Column(name = "parentId")
	private Long parentId;

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}


    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return this.id;
    }

}
