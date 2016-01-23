package com.oracle2couchbase;

public class PK_Column implements Comparable {
	
	private int columnIndex;
	private int keyIndex;
	private String name;
	private int type;
	
	public PK_Column(int columnIndex, int keyIndex, String name, int type) {
		super();
		this.columnIndex = columnIndex;
		this.keyIndex = keyIndex;
		this.name = name;
		this.type = type;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public int getKeyIndex() {
		return keyIndex;
	}

	public void setKeyIndex(int keyIndex) {
		this.keyIndex = keyIndex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

		@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PK_Column other = (PK_Column) obj;
		if (columnIndex != other.columnIndex)
			return false;
		if (keyIndex != other.keyIndex)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
		
	@Override
	public String toString() {
		return "PK_Column [columnIndex=" + columnIndex + ", keyIndex=" + keyIndex + ", name=" + name + ", type=" + type
				+ "]";
	}
	
	@Override
	public int compareTo(Object o) {
		return this.getKeyIndex() - ((PK_Column)o).getKeyIndex();
	}
	
}
