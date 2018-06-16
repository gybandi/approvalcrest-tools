package com.gybandi.approvalcrest.tools.pojo;

public class TestPojo {

	private Long id;
	private String name;
	private Integer integerProperty;
	private Boolean booleanProperty;
	private TestPojo parent;
	
	public TestPojo(Long id, String name, Integer integerProperty, Boolean booleanProperty, TestPojo parent) {
		this.id = id;
		this.name = name;
		this.integerProperty = integerProperty;
		this.booleanProperty = booleanProperty;
		this.parent = parent;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getIntegerProperty() {
		return integerProperty;
	}
	public void setIntegerProperty(Integer integerProperty) {
		this.integerProperty = integerProperty;
	}
	public Boolean getBooleanProperty() {
		return booleanProperty;
	}
	public void setBooleanProperty(Boolean booleanProperty) {
		this.booleanProperty = booleanProperty;
	}
	public TestPojo getParent() {
		return parent;
	}
	public void setParent(TestPojo parent) {
		this.parent = parent;
	}
	
}
