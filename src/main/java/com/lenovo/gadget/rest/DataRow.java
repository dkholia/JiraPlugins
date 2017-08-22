package com.lenovo.gadget.rest;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@XmlRootElement
@XmlType(namespace = "com.lenovo.gadget.rest.CreatedVsResolvedVsClosedResource")
public  class DataRow {
	private Object key;
	/*@XmlElement
	private String createdUrl;*/
	@XmlElement
	private int createdValue;
	/*@XmlElement
	private String resolvedUrl;*/
	@XmlElement
	private int resolvedValue;
	@XmlElement
	private Integer trendCount;
	@XmlElement(name = "key")
	private String keyString;
	/*@XmlElement
	private String closedUrl;*/
	@XmlElement
	private Integer closedValue;
	

	public DataRow() {
	}

	DataRow(final Object key,  final int createdValue,  final int resolvedValue, int closedValue) {
		this.key = key;
		this.createdValue = createdValue;
		this.resolvedValue = resolvedValue;
		this.keyString = key.toString();
		this.closedValue = closedValue;
	}

	public String getKey() {
		return key.toString();
	}

	public Object getRawKey() {
		return key;
	}


	public int getCreatedValue() {
		return createdValue;
	}


	public int getResolvedValue() {
		return resolvedValue;
	}

	public Integer getTrendCount() {
		return trendCount;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public String toString() {
		return "DataRow [key=" + key + ", createdValue=" + createdValue + ", resolvedValue=" + resolvedValue
				+ ", trendCount=" + trendCount + ", keyString=" + keyString + ", closedValue=" + closedValue + "]";
	}
}