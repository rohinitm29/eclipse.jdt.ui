/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 *
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.astview.views;

import java.util.List;

import org.eclipse.swt.graphics.Image;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

public class NodeProperty extends ASTAttribute {
	
	private ASTNode fParent;
	private StructuralPropertyDescriptor fProperty;
	
	public NodeProperty(ASTNode parent, StructuralPropertyDescriptor property) {
		fParent= parent;
		fProperty= property;
	}
	
	@Override
	public Object getParent() {
		return fParent;
	}
	
	@Override
	public Object[] getChildren() {
		Object child= getNode();
		if (child instanceof List) {
			return ((List<?>) child).toArray();
		} else if (child instanceof ASTNode) {
			return new Object[] { child };
		}
		return EMPTY;
	}

	@Override
	public String getLabel() {
		StringBuilder buf= new StringBuilder();
		buf.append(getPropertyName());
		
		if (fProperty.isSimpleProperty()) {
			buf.append(": "); //$NON-NLS-1$
			Object node= getNode();
			if (node != null) {
				buf.append('\'');
				buf.append(getNode().toString());
				buf.append('\'');
			} else {
				buf.append("null"); //$NON-NLS-1$
			}
		} else if (fProperty.isChildListProperty()) {
			List<?> node= (List<?>) getNode();
			buf.append(" (").append(node.size()).append(')'); //$NON-NLS-1$
		} else { // child property
			if (getNode() == null) {
				buf.append(": null"); //$NON-NLS-1$
			}
		}
		return buf.toString();
	}

	@Override
	public Image getImage() {
		return null;
	}
	
	public Object getNode() {
		return fParent.getStructuralProperty(fProperty);
	}
	
	public String getPropertyName() {
		return toConstantName(fProperty.getId());
	}
	
	private static String toConstantName(String string) {
		StringBuilder buf= new StringBuilder();
		for (int i= 0; i < string.length(); i++) {
			char ch= string.charAt(i);
			if (i != 0 && Character.isUpperCase(ch)) {
				buf.append('_');
			}
			buf.append(Character.toUpperCase(ch));
		}
		return buf.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || !o.getClass().equals(getClass())) {
			return false;
		}
		NodeProperty castedObj= (NodeProperty) o;
		return  fParent.equals(castedObj.fParent) && (fProperty == castedObj.fProperty);
	}

	@Override
	public int hashCode() {
		return fParent.hashCode() * 31 + fProperty.hashCode();
	}
	
	@Override
	public String toString() {
		return getLabel();

	}
}
