package com.oracle2couchbase;

public class Column implements Comparable {
	
	private int index;
	private String name;
	private int type;
	
	public Column(int index, String name, int type) {
		super();
		this.index = index;
		this.name = name;
		this.type = type;
	}


	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
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
	public String toString() {
		return "Column [index=" + index + ", name=" + name + ", type=" + type + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Column other = (Column) obj;
		if (index != other.index)
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
	public int compareTo(Object o) {
		return this.getIndex() - ((Column)o).getIndex();
	}
	
}
