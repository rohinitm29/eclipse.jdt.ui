/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
package org.eclipse.jdt.ui.tests.refactoring;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.internal.corext.refactoring.base.ChangeContext;
import org.eclipse.jdt.internal.corext.refactoring.base.IChange;
import org.eclipse.jdt.internal.corext.refactoring.base.RefactoringStatus;
import org.eclipse.jdt.internal.corext.refactoring.sef.SelfEncapsulateFieldRefactoring;

import org.eclipse.jdt.ui.tests.refactoring.infra.TestExceptionHandler;

public class SefTests extends AbstractSelectionTestCase {

	private static SefTestSetup fgTestSetup;
	
	public SefTests(String name) {
		super(name);
	}
	
	public static Test suite() {
		fgTestSetup= new SefTestSetup(new TestSuite(SefTests.class));
		return fgTestSetup;
	}

	protected IPackageFragmentRoot getRoot() {
		return fgTestSetup.getRoot();
	}
	
	protected String getResourceLocation() {
		return "SefWorkSpace/SefTests/";
	}
	
	protected String adaptName(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1) + ".java";
	}	
	
	private IPackageFragment getObjectPackage() throws JavaModelException {
		return fgTestSetup.getObjectPackage();
 	}
	
	private IPackageFragment getBasePackage() throws JavaModelException {
		return fgTestSetup.getBasePackage();
 	}
	
	protected void performTest(IPackageFragment packageFragment, String id, String outputFolder, String fieldName) throws Exception {
		IProgressMonitor pm= new NullProgressMonitor();
		ICompilationUnit unit= createCU(packageFragment, id);
		IField field= getField(unit, fieldName);
		assertNotNull(field);
		SelfEncapsulateFieldRefactoring refactoring= new SelfEncapsulateFieldRefactoring(field, 4);
		RefactoringStatus status= refactoring.checkPreconditions(pm);
		assertTrue(!status.hasFatalError());
		IChange change= refactoring.createChange(pm);
		assertNotNull(change);
		ChangeContext context= new ChangeContext(new TestExceptionHandler());
		change.aboutToPerform(context, new NullProgressMonitor());
		change.perform(context, pm);
		change.performed();
		assertNotNull(change.getUndoChange());
		String source= unit.getSource();
		String out= getProofedContent(outputFolder, id);
		assertTrue(compareSource(source, out));
	}	
	
	private static IField getField(ICompilationUnit unit, String fieldName) throws Exception {
		IField result= null;
		IType[] types= unit.getAllTypes();
		for (int i= 0; i < types.length; i++) {
			IType type= types[i];
			result= type.getField(fieldName);
			if (result != null && result.exists())
				break;
		}
		return result;
	}

	private void objectTest(String fieldName) throws Exception {
		performTest(getObjectPackage(), getName(), "object_out", fieldName);
	}
	
	//=====================================================================================
	// Basic Object Test
	//=====================================================================================
	
	public void testSimpleRead() throws Exception {
		objectTest("field");
	}
	
	public void testSimpleWrite() throws Exception {
		objectTest("field");
	}
	
	public void testSimpleReadWrite() throws Exception {
		objectTest("field");
	}
	
	public void testNestedRead() throws Exception {
		objectTest("field");
	}
}