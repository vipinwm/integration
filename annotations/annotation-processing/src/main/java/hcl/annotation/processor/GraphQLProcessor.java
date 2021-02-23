/**
 * 
 */
package hcl.annotation.processor;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.ElementScanner6;
import javax.lang.model.util.ElementScanner8;
import javax.lang.model.util.SimpleTypeVisitor7;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

import hcl.annotation.graphql.VGraphQLQuery;
import hcl.annotation.graphql.VGraphQLType;
import hcl.annotation.processor.graphql.Param;
import hcl.annotation.processor.graphql.QueryInfo;
import hcl.annotation.processor.graphql.Type;
import hcl.annotation.processor.graphql.TypeField;
import hcl.annotation.processor.impl.GraphQLSchemaBuilder;

import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * @author vipink
 *
 */
@SupportedAnnotationTypes({
		"hcl.annotation.graphql.VGraphQLArgument",
		"hcl.annotation.graphql.VGraphQLQuery",
		"hcl.annotation.graphql.VGraphQLType" })
public class GraphQLProcessor extends AbstractProcessor {

	/**
	 * This is going to store all the possible types including defaults
	 */
	private Map<String,String> dataTypes = new HashMap<>();
	private Map<String, List<VGraphQLType>> processingResults;
	private Map<String, String> processingTypeName;
	private Map<String,QueryInfo> queries = new HashMap<>();
	private Map<String,Type> types = new HashMap<>();
	private boolean resetResults = true;
	private boolean openFile = true;


	/**
	 * 
	 */
	public GraphQLProcessor() {
		dataTypes.put("java.lang.String", "String");
		dataTypes.put("java.lang.Boolean", "Boolean");
		dataTypes.put("java.lang.Long", "Int");
		dataTypes.put("java.lang.Float", "Float");
		dataTypes.put("java.lang.Integer", "Int");
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		System.out.println(" Round "+resetResults);
		if (resetResults) {
			processingResults = new HashMap<>();
			processingTypeName = new HashMap<>();
			
		}
		// load all the custom dynamic types
		loadCustomTypes(roundEnv);

		buildQueryInfo(roundEnv);
		
		buildTypes(roundEnv);
		
		// finished collecting the type.. now generate a file
		return postProcessWorkItemDefinition();
	}
	
	private void buildTypes(RoundEnvironment roundEnv) {
		for (Element element : roundEnv.getElementsAnnotatedWith(VGraphQLType.class)) {
			
			if (element instanceof TypeElement) {
				
				TypeElement typeElement = (TypeElement) element;
				Type type = new Type(typeElement.getSimpleName().toString());
				types.put(typeElement.asType().toString(), type);
				processingResults.put(typeElement.asType().toString(), new ArrayList<>());

				if (typeElement.getKind() == ElementKind.CLASS) {
					buildTypeAttributes(type, typeElement);
					///processingTypeName.put(typeElement.asType().toString(), schema);
				}
				// done processing type
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
						MessageFormat.format("Wid Processor : processing class {0}.", typeElement.asType().toString()));

				if (typeElement.getInterfaces() != null && typeElement.getInterfaces().size() > 0) {
					for (TypeMirror mirror : typeElement.getInterfaces()) {
						if (mirror.getAnnotation(VGraphQLType.class) != null) {
							processingResults.get(typeElement.asType().toString())
									.add(mirror.getAnnotation(VGraphQLType.class));
						}
					}
				}

				processingResults.get(typeElement.asType().toString())
						.add(typeElement.getAnnotation(VGraphQLType.class));
			}
		}
	}

	private void buildQueryInfo(RoundEnvironment roundEnv) {
		for (Element element : roundEnv
				.getElementsAnnotatedWith(VGraphQLQuery.class)) {
			if (element instanceof ExecutableElement) {
				ExecutableElement typeElement = (ExecutableElement) element;
				if (typeElement.getKind() == ElementKind.METHOD) {
					buildQuery(typeElement.toString(), typeElement);
				}
			}
		}
	}

	private String buildType(String typeName, TypeElement typeElement) {
		StringBuilder builder = new StringBuilder();
		builder.append("type ").append(typeName).append(" {").append("\r\n");
		for (Element enclosedElement : typeElement.getEnclosedElements()) {
			if (enclosedElement.getKind() == ElementKind.FIELD) {
				// see if you want to process modifier
				Set<Modifier> modifiers = enclosedElement.getModifiers();
				//System.out.println(enclosedElement.asType().toString());
				TypeMirror typeMirror = enclosedElement.asType();
				TypeKind returnKind = typeMirror.getKind();
				//System.out.println("L" + returnKind);
				if (returnKind == TypeKind.DECLARED) {
					DeclaredType declaredReturnType = (DeclaredType) typeMirror;
					String dtype = declaredReturnType.toString();
					if (dtype.startsWith("java.util.List<")) {
						List<? extends TypeMirror> mL = declaredReturnType.getTypeArguments();
						for (TypeMirror m : declaredReturnType.getTypeArguments()) {
							//System.out.println(m);
							builder.append(enclosedElement.getSimpleName()).append(": [")
							.append(dataTypes.get(m.toString())).append("]\r\n");
						}
					} else {
						builder.append(enclosedElement.getSimpleName()).append(": ")
								.append(dataTypes.get(enclosedElement.asType().toString())).append("\r\n");
					}
				}

			}
		}
		builder.append("} \n\r \n\r");
		return builder.toString();
	}
	
	private void buildTypeAttributes(Type type, TypeElement typeElement) {
		StringBuilder builder = new StringBuilder();
		for (Element enclosedElement : typeElement.getEnclosedElements()) {
			if (enclosedElement.getKind() == ElementKind.FIELD) {
				TypeField param = new TypeField();
				
				// see if you want to process modifier
				Set<Modifier> modifiers = enclosedElement.getModifiers();
				//System.out.println(enclosedElement.asType().toString());
				TypeMirror typeMirror = enclosedElement.asType();
				TypeKind returnKind = typeMirror.getKind();
				if (returnKind == TypeKind.DECLARED) {
					DeclaredType declaredReturnType = (DeclaredType) typeMirror;
					String dtype = declaredReturnType.toString();
					if (dtype.startsWith("java.util.List<")) {
						List<? extends TypeMirror> mL = declaredReturnType.getTypeArguments();
						for (TypeMirror m : declaredReturnType.getTypeArguments()) {
							param.setName(enclosedElement.getSimpleName().toString());
							param.setType(dataTypes.get(m.toString()));
							param.setSingle(false);
						}
					} else {
						param.setName(enclosedElement.getSimpleName().toString());
						param.setType(dataTypes.get(enclosedElement.asType().toString()));
						param.setSingle(true);
					}
				}
				type.getAttributes().put(param.getName(),param);
			}
		}
	}
	
	private void buildQuery(String typeName , ExecutableElement typeElement ) {
		QueryInfo queryInfo = new QueryInfo();
		queryInfo.setMethodName(typeElement.getSimpleName().toString());
		if (typeElement.getReturnType() != null) {
			String returnType = typeElement.getReturnType().toString();
			returnType = returnType.substring(returnType.lastIndexOf(".")+1);
			queryInfo.setReturnType(returnType);
		}
		// build query method here
		for (VariableElement el : typeElement.getParameters()) {
			Param p = new Param();
			p.setName(el.toString());
			// set type
			p.setType(dataTypes.get(el.asType().toString()));
			// use annotation if there is any
			for (AnnotationMirror e2 : el.getAnnotationMirrors()) {
				Map<? extends ExecutableElement, ? extends AnnotationValue>  map =  e2.getElementValues();
				for (ExecutableElement e : map.keySet()) {
					if ("name".equalsIgnoreCase(e.getSimpleName().toString())) {
						p.setName(map.get(e).toString().replaceAll("\"", ""));
					} else if ("required".equalsIgnoreCase(e.getSimpleName().toString())) {
						String req = map.get(e).toString();
						if ("true".equalsIgnoreCase(req)) {
							p.setRequired(true);
						}
					}
				}
			}
			queryInfo.getParamsList().add(p);
		}
		// find the method name from annotation
		for (AnnotationMirror el : typeElement.getAnnotationMirrors()) {
			Map<? extends ExecutableElement, ? extends AnnotationValue>  map =  el.getElementValues();
			for (ExecutableElement e : map.keySet()) {
				if ("name".equalsIgnoreCase(e.getSimpleName().toString())) {
					queryInfo.setMethodName(map.get(e).toString().replaceAll("\"", ""));
				}
			}
		}
		queries.put(queryInfo.getMethodName(), queryInfo);
	}

	public boolean postProcessWorkItemDefinition() {
		if (types == null || types.size() < 1) {
			return false;
		}
		try {
			SchemaBuilder schemaBuider = new GraphQLSchemaBuilder(); 
			String schema = schemaBuider.generateSchema(queries, types);
			if (schema != null) {
				write(schema);
			}
		} catch (Exception e) {
			e.printStackTrace();
			processingEnv.getMessager().printMessage(
					Diagnostic.Kind.ERROR,
					MessageFormat.format(
							"Error post-processing graphql annotations: {0}.",
							e.getMessage()));
			return false;
		}
		return true;
	}

	public String generateSchema(Map<String,QueryInfo> queries,
			Map<String,Type> types)
			throws IOException {
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

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

	private void write(String schema) throws IOException {
		System.out.println(" openFile "+openFile);
		if (openFile) {
			FileObject f = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "graphql.schema");
			try (Writer w = new OutputStreamWriter(f.openOutputStream(), StandardCharsets.UTF_8)) {
				w.write(schema);
			}
			openFile = false;
		}
	}

	private Element asElement(TypeMirror m) {
		return processingEnv.getTypeUtils().asElement(m);
	}
	
	private void loadCustomTypes(RoundEnvironment roundEnv){
		try {
			// store the scanned types 
			ElementScanner8<Void, Void> scanner = new ElementScanner8<Void, Void>() {
				@Override
				public Void visitType(TypeElement e, Void aVoid) {
					String className = e.toString();
					if (className.lastIndexOf(".") != -1) {
						String typeObject = className.substring(className
								.lastIndexOf(".")+1);
						processingEnv.getMessager().printMessage(
								Diagnostic.Kind.NOTE,
								MessageFormat.format(
										"type {0} and name is {1}.",
										className,typeObject));
						dataTypes.put(className, typeObject);
					}
					return super.visitType(e, aVoid);
				}

				@Override
				public Void visitUnknown(Element e, Void aVoid) {
					return DEFAULT_VALUE;
				}
			};

			for (Element e : roundEnv.getRootElements()) {
				// ignore packages during run
				if (e.getKind() == ElementKind.PACKAGE) { 
					continue;
				}
				scanner.scan(e, null);
			}
		} catch (RuntimeException | Error e) {
			// javac sucks at reporting errors in annotation processors
			e.printStackTrace();
		}
	}
	

}
