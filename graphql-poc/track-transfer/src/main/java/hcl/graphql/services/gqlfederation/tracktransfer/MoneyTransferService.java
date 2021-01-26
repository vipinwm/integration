package hcl.graphql.services.gqlfederation.tracktransfer;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hcl.graphql.services.rest.RestCallService;

@Service
public class MoneyTransferService {
	@Autowired
	private RestCallService restCallService;

	@NotNull
	public MoneyTransfer lookupTransfer(@NotNull String mtcn) {

		return getMoneyTransfer(mtcn) != null ? getMoneyTransfer(mtcn) : DataHelper.findTransferByMtcn(mtcn);
	}

	public Order findMoneySentByMtcn(String mtcn) {
		return getMoneyTransfer(mtcn) != null ? getMoneyTransfer(mtcn).getOrder()
				: DataHelper.findTransferByMtcn(mtcn).getOrder();
	}

	private MoneyTransfer getMoneyTransfer(String mtcn) {
		MoneyTransfer moneyTransfer = restCallService.invokeLookupTransfer(mtcn);
		if (moneyTransfer != null) {
			return moneyTransfer;
		}
		return null;
	}
}