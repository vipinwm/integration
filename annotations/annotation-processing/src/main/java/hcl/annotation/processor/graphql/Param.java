/**
 * 
 */
package hcl.annotation.processor.graphql;

/**
 * @author vipink
 *
 */
public class Param {

	/**
	 * 
	 */
	public Param() {}

	private String name;
	private String type;
	private boolean required;
	private String defaultValue;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@Override
	public String toString() {
		return "Param [name=" + name + ", type=" + type + ", required="
				+ required + "]";
	}
	public Param(String name, String type, boolean required) {
		super();
		this.name = name;
		this.type = type;
		this.required = required;
	}
	
	
}
