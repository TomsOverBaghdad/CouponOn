package com.example.couponon;

//stores the name and the id of the list, universal for product and coupon lists since its just a name and a list id
public class MyList {
	private String name;
	private int id;
	
	public MyList(String name, int id){
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyList other = (MyList) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
