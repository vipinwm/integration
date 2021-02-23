/**
 * 
 */
package hcl.annotation.processor.graphql;

import java.util.HashMap;
import java.util.Map;

/**
 * @author developer
 *
 */
public class Type {

	/**
	 * 
	 */
	public Type() {}

	private String name;
	private Map<String,TypeField> attributes = new HashMap<>();
	private Map<String,String> annotations = new HashMap<>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, TypeField> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, TypeField> attributes) {
		this.attributes = attributes;
	}
	public Map<String, String> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(Map<String, String> annotations) {
		this.annotations = annotations;
	}
	public Type(String name) {
		super();
		this.name = name;
	}
	
}
