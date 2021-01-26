package hcl.graphql.services.gqlfederation.tracktransfer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hcl.graphql.services.rest.RestCallService;

@Service
public class OrderService {

	private List<Order> moneySentList = new ArrayList<>();
	@Autowired
	private RestCallService restCallService;

	@PostConstruct
	public void init() {
		moneySentList = DataHelper.loadMoneySentList();
	}

	@NotNull
	public Order lookupMoneySent(@NotNull String id) {

		if (restCallService.invokeLookupOrder(id) != null) {
			return restCallService.invokeLookupOrder(id).stream().filter(m -> m.getId().equals(id)).findAny().get();
		}

		return moneySentList.stream().filter(m -> m.getId().equals(id)).findAny().get();
	}

	public List<Order> findMoneySentByUserId(String userId) {

		if (restCallService.invokeLookupOrder(userId) != null) {
			return restCallService.invokeLookupOrder(userId);
		}

		return DataHelper.findMoneySentByUserId(userId);
	}

	public Address findToaddressByMoneySentId(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public Address findFromaddressByMoneySentId(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}