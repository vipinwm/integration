/**
 * 
 */
package hcl.annotation.processor.impl;

import java.io.IOException;
import java.util.Map;

import hcl.annotation.processor.SchemaBuilder;
import hcl.annotation.processor.graphql.Param;
import hcl.annotation.processor.graphql.QueryInfo;
import hcl.annotation.processor.graphql.Type;
import hcl.annotation.processor.graphql.TypeField;

/**
 * @author developer
 *
 */
public class ApolloSchemaBuilder implements SchemaBuilder {

	/**
	 * 
	 */
	public ApolloSchemaBuilder() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see hcl.annotation.processor.SchemaBuilder#generateSchema(java.util.Map, java.util.Map)
	 */
	@Override
	public String generateSchema(Map<String, QueryInfo> queries, Map<String, Type> types) throws IOException {
		StringBuilder buildr = new StringBuilder();
		if (queries.size() > 0) {
			buildr.append("type Query {").append("\r\n");
			for (String key : queries.keySet()) {
				QueryInfo info = queries.get(key);
				buildr.append(info.getMethodName()).append(" ( ");
				int k = 0;
				for (Param p: info.getParamsList()) {
					if (k>0) {
						buildr.append(", ");
					}
					buildr.append(p.getName()).append(": ").append(p.getType());
					if (p.isRequired()) {
						buildr.append("!");
					}
					k++;
				}
				buildr.append(" )");
				buildr.append(": ").append(info.getReturnType()).append("\r\n");
			}
			
			buildr.append("}").append("\n\r");
		}
		
		if (types.size() > 0) {
			for (Type type: types.values()) {
				buildr.append("type ").append(type.getName()).append(" {").append("\r\n");
				for (TypeField field : type.getAttributes().values()) {
					if (field.isSingle()) {
						buildr.append(field.getName()).append(": ")
						.append(field.getType()).append("\r\n");
					} else {
						buildr.append(field.getName()).append(": [")
						.append(field.getType()).append("]\r\n");
					}
				}
				buildr.append("} \n\r \n\r");
			}
		}
		return buildr.toString();
	}

}
