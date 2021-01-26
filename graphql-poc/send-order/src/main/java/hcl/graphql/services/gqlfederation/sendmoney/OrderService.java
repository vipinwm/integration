package hcl.graphql.services.gqlfederation.sendmoney;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hcl.graphql.services.rest.RestCallService;

@Service
public class OrderService {

	private List<Order> orderList = new ArrayList<>();
	@Autowired
	private RestCallService restCallService;

	@PostConstruct
	public void init() {
		orderList = DataHelper.loadOrderList();
	}

	@NotNull
	public Order lookupOrder(@NotNull String id) {

		List<Order> orders = getOrders(id);
		if (orders != null && !orders.isEmpty()) {
			return orders.stream().filter(m -> m.getId().equals(id)).findAny().get();
		}
		return orderList.stream().filter(m -> m.getId().equals(id)).findAny().get();
	}

	public List<Order> findOrdersByUserId(String userId) {

		List<Order> orders = getOrders(userId);
		if (orders != null) {
			return orders;
		}
		return DataHelper.findOrderByUserId(userId);
	}

	public Address findToaddressByOrderId(String id) {

		List<Order> orders = getOrders(id);
		if (orders != null && !orders.isEmpty()) {
			Order order = orders.stream().filter(m -> m.getId().equals(id)).findAny().get();
			if (order != null && order.getToaddress() != null) {
				return order.getToaddress();
			}
		}

		return lookupOrder(id).getToaddress();
	}

	public Address findFromaddressByOrderId(String id) {
		return lookupOrder(id).getFromaddress();
	}

	private List<Order> getOrders(String userId) {
		if (restCallService.invokeLookupUser(userId) != null) {
			User user1 = restCallService.invokeLookupUser(userId).stream().filter(user -> user.getId().equals(userId))
					.findAny().get();
			if (user1 != null && user1.getOrders() != null) {
				return user1.getOrders();
			}
		}
		return null;
	}
}