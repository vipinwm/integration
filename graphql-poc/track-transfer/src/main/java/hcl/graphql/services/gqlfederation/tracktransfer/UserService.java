package hcl.graphql.services.gqlfederation.tracktransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hcl.graphql.services.rest.RestCallService;

@Service
public class UserService {

    private List<User> users = new ArrayList<>();
    private Map<User, Address> userAddressMap = new HashMap<>();
    @Autowired
	private RestCallService restCallService;

    @PostConstruct
    public void init() {
    	users = DataHelper.loadUser();
    }

    @NotNull
    public User lookupUser(@NotNull String id) {
    	
    	if(restCallService.invokeLookupUser(id)!=null && !restCallService.invokeLookupUser(id).isEmpty())
    	{
    		return restCallService.invokeLookupUser(id).stream().filter(user -> user.getId().equals(id)).findAny().get();
    	}
    	
    	return users.stream().filter(user -> user.getId().equals(id)).findAny().get();
    }

}