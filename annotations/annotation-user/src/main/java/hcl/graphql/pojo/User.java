/**
 * 
 */
package hcl.graphql.pojo;

import java.util.List;

import hcl.annotation.graphql.VGraphQLType;

/**
 * @author vipink
 *
 */
@VGraphQLType (name="user", key="id", client="apollo")
public class User {

	/**
	 * 
	 */
	public User() {}
	
	private String firstName;
	private String id;
	private List<Order> orders;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
	
	

}
