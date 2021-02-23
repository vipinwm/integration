/**
 * 
 */
package hcl.annotation.processor;

import java.io.IOException;
import java.util.Map;

import hcl.annotation.processor.graphql.QueryInfo;
import hcl.annotation.processor.graphql.Type;

/**
 * @author developer
 *
 */
public interface SchemaBuilder {
	public String generateSchema(Map<String,QueryInfo> queries,
			Map<String,Type> types)
			throws IOException;
}
