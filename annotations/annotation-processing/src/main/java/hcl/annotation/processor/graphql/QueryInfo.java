/**
 * 
 */
package hcl.annotation.processor.graphql;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vipink
 *
 */
public class QueryInfo {

	private String returnType;
	private String name;
	private List<Param> paramsList = new ArrayList<>();
	/**
	 * 
	 */
	public QueryInfo() {
		// TODO Auto-generated constructor stub
	}
	public QueryInfo(String returnType, String methodName) {
		super();
		this.returnType = returnType;
		this.name = methodName;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public String getMethodName() {
		return name;
	}
	public void setMethodName(String methodName) {
		this.name = methodName;
	}
	public List<Param> getParamsList() {
		return paramsList;
	}
	public void setParamsList(List<Param> paramsList) {
		this.paramsList = paramsList;
	}
	@Override
	public String toString() {
		return "QueryInfo [returnType=" + returnType + ", methodName="
				+ name + ", paramsList=" + paramsList + "]";
		
	}
	

}
