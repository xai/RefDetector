package br.ufmg.dcc.labsoft.refactoringanalyzer.util;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;

public class AstUtils {

	private AstUtils() {
		//
	}
	
	public static String getKeyFromMethodBinding(IMethodBinding binding) {
		StringBuilder sb = new StringBuilder();
		String className = binding.getDeclaringClass().getErasure().getQualifiedName();
		sb.append(className);
		sb.append('#');
		String methodName = binding.getName();
		sb.append(methodName);
		//if (methodName.equals("allObjectsSorted")) {
		//	System.out.println();
		//}
		sb.append('(');
		ITypeBinding[] parameters = binding.getParameterTypes();
		for (int i = 0; i < parameters.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			ITypeBinding type = parameters[i];
			sb.append(type.getErasure().getName());
		}
		sb.append(')');
		return sb.toString();
	}
	
	public static String getSignatureFromMethodDeclaration(MethodDeclaration methodDeclaration) {
		String methodName = methodDeclaration.getName().getIdentifier();
//		if (methodName.equals("allObjectsSorted")) {
//			System.out.println();
//		}
		StringBuilder sb = new StringBuilder();
		sb.append(methodName);
		sb.append('(');
		Iterator<SingleVariableDeclaration> parameters = methodDeclaration.parameters().iterator();
		while (parameters.hasNext()) {
			SingleVariableDeclaration parameter = parameters.next();
			Type parameterType = parameter.getType();
			String rawTypeName = stripQualifiedTypeName(stripTypeParamsFromTypeName(parameterType.toString()));
			sb.append(rawTypeName);
			for (int i = parameter.getExtraDimensions(); i > 0; i--) {
				sb.append("[]");
			}
			if (parameter.isVarargs()) {
				sb.append("[]");
			}
			if (parameters.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append(')');
		String methodSignature = sb.toString();
		return methodSignature;
	}

	public static String stripTypeParamsFromTypeName(String typeNameWithGenerics) {
		String rawTypeName = typeNameWithGenerics;
		int startOfTypeParams = typeNameWithGenerics.indexOf('<');
		if (startOfTypeParams >= 0) {
			rawTypeName = typeNameWithGenerics.substring(0, startOfTypeParams);
			int endOfTypeParams = typeNameWithGenerics.lastIndexOf('>');
			if (endOfTypeParams > startOfTypeParams && endOfTypeParams < typeNameWithGenerics.length() - 1) {
				rawTypeName = rawTypeName + typeNameWithGenerics.substring(endOfTypeParams + 1);
			}
		}
		return rawTypeName;
	}
	
	public static String stripQualifiedTypeName(String qualifiedTypeName) {
		int dotPos = qualifiedTypeName.lastIndexOf('.');
		if (dotPos >= 0) {
			return qualifiedTypeName.substring(dotPos + 1);
		}
		return qualifiedTypeName;
	}
	
	public void x(List<String>[] lists) {
		
	}
	
	public static int countNumberOfStatements(MethodDeclaration decl) {
		return new StatementCounter().countStatements(decl);
	}
	
	private static class StatementCounter extends ASTVisitor {
		private int counter;
		public int countStatements(MethodDeclaration methodDeclaration) {
			counter = 0;
			methodDeclaration.accept(this);
			return counter;
		}
		@Override
		public void preVisit(ASTNode node) {
			if (node instanceof Statement && !(node instanceof Block)) {
				counter++;
			}
		}
	}
}