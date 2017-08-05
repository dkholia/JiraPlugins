package com.lenovo.gadget.rest;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement
    //have to define a namespace here, since there's other 'DataRow' JAXB beans
    @XmlType(namespace = "com.atlassian.jira.gadgets.system.CreatedVsResolvedResource")
    public  class DataRow {
        private Object key;
        @XmlElement
        private String createdUrl;
        @XmlElement
        private int createdValue;
        @XmlElement
        private String resolvedUrl;
        @XmlElement
        private int resolvedValue;
        @XmlElement
        private Integer trendCount;
        @XmlElement(name = "key")
        private String keyString;

        public DataRow() {
        }

        DataRow(final Object key, final String createdUrl, final int createdValue, final String resolvedUrl, final int resolvedValue, Integer trendCount) {
            this.key = key;
            this.createdUrl = createdUrl;
            this.createdValue = createdValue;
            this.resolvedUrl = resolvedUrl;
            this.resolvedValue = resolvedValue;
            this.trendCount = trendCount;
            this.keyString = key.toString();
        }

        public String getKey() {
            return key.toString();
        }

        public Object getRawKey() {
            return key;
        }

        public String getCreatedUrl() {
            return createdUrl;
        }

        public int getCreatedValue() {
            return createdValue;
        }

        public String getResolvedUrl() {
            return resolvedUrl;
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
			return "DataRow [key=" + key + ", createdUrl=" + createdUrl + ", createdValue=" + createdValue
					+ ", resolvedUrl=" + resolvedUrl + ", resolvedValue=" + resolvedValue + ", trendCount=" + trendCount
					+ ", keyString=" + keyString + "]";
		}

       
    }