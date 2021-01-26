package hcl.graphql.services.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import hcl.graphql.services.gqlfederation.tracktransfer.MoneyTransfer;
import hcl.graphql.services.gqlfederation.tracktransfer.Order;
import hcl.graphql.services.gqlfederation.tracktransfer.User;

@Component
public class RestCallService {

	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;

	@Value("${invokeLookupTransfer.rule.url}")
	private String invokeLookupTransfeURL;
	
	@Value("${invokeLookupUser.rule.url}")
	private String invokeLookupUserURL;
	
	@Value("${invokeLookupOrder.rule.url}")
	private String invokeLookupOrderURL;

	public MoneyTransfer invokeLookupTransfer(String mtcn) {
		MoneyTransferResponse moneyTransferResponse = null;
		try {
			moneyTransferResponse = restTemplate.getForObject(invokeLookupTransfeURL, MoneyTransferResponse.class);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return moneyTransferResponse != null ? moneyTransferResponse.getMoneyTransfer() : null;

	}
	
	public List<User> invokeLookupUser(String id) {
		UserResponse userResponse = null;
		try {
			userResponse = restTemplate.getForObject(invokeLookupUserURL, UserResponse.class);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return userResponse != null ? userResponse.getUsers() : null;

	}
	
	
	
	public List<Order> invokeLookupOrder(String id) {
		OrderResponse orderResponse = null;
		try {
			orderResponse = restTemplate.getForObject(invokeLookupOrderURL, OrderResponse.class);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return orderResponse != null ? orderResponse.getOrders() : null;

	}
}
