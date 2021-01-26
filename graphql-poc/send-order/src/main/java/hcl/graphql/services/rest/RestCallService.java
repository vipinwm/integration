package hcl.graphql.services.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import hcl.graphql.services.gqlfederation.sendmoney.User;

@Component
public class RestCallService {

	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;

	@Value("${invokeMoneySentByUserId.rule.url}")
	private String invokeMoneySentByUserIdURL;

	public List<User> invokeLookupUser(String id) {
		UserListResponse userListResponse =null;
		try{
			userListResponse = restTemplate.getForObject(invokeMoneySentByUserIdURL, UserListResponse.class);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		return  userListResponse!=null? userListResponse.getUsers():null;

	}
}
