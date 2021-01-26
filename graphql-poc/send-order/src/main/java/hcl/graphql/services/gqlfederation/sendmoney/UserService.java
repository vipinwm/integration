package hcl.graphql.services.gqlfederation.sendmoney;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hcl.graphql.services.rest.RestCallService;

@Service
public class UserService {

    private List<User> users = null;
    @Autowired
    private RestCallService restCallService;

    @PostConstruct
    public void init() {
    	users = DataHelper.loadUser();
    }

    @NotNull
    public User lookupUser(@NotNull String id) {
       // User user1 = users.stream().filter(user -> user.getId().equals(id)).findAny().get();
       // return user1;
    	
    	if(restCallService.invokeLookupUser(id)!=null)
    	{
    		return restCallService.invokeLookupUser(id).stream().filter(user -> user.getId().equals(id)).findAny().get();
    	}
        
        return users.stream().filter(user -> user.getId().equals(id)).findAny().get();
    }

}