/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
package org.eclipse.jdt.ui.tests.actions;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.internal.corext.SourceRange;
import org.eclipse.jdt.internal.ui.javaeditor.selectionactions.StructureSelectEnclosingAction;
import org.eclipse.jdt.internal.ui.javaeditor.selectionactions.StructureSelectNextAction;
import org.eclipse.jdt.internal.ui.javaeditor.selectionactions.StructureSelectPreviousAction;

import org.eclipse.jdt.ui.tests.refactoring.AbstractSelectionTestCase;
import org.eclipse.jdt.ui.tests.refactoring.MySetup;
import org.eclipse.jdt.ui.tests.refactoring.RefactoringTest;
import org.eclipse.jdt.ui.tests.refactoring.infra.TextRangeUtil;
public class StructureSelectionActionTests extends RefactoringTest{
	
	private static final Class clazz= StructureSelectionActionTests.class;
	private static final String REFACTORING_PATH= "StructureSelectionAction/";
	
	public StructureSelectionActionTests(String name){
		super(name);
	}
	
	protected String getRefactoringPath() {
		return REFACTORING_PATH;
	}
	
	public static Test suite() {
		return new MySetup(new TestSuite(clazz));
	}
	
	private String getSimpleTestFileName(boolean input){
		String fileName = "A_" + getName();
		fileName += input ? "": "_out";
		fileName +=  input ? ".java": ".txt"; 
		return fileName;
	}
	
	private String getTestFileName(boolean input){
		return TEST_PATH_PREFIX + getRefactoringPath() + getSimpleTestFileName(input);
	}
	
	private String getPassingTestFileName(boolean input){
		return getTestFileName(input);
	}
	
	//------------
	protected ICompilationUnit createCUfromTestFile(IPackageFragment pack, boolean input) throws Exception {
		return createCU(pack, getSimpleTestFileName(input), getFileContents(getTestFileName(input)));
	}
	
	private ISourceRange getSelection(ICompilationUnit cu) throws Exception{
		String source= cu.getSource();
		int offset= source.indexOf(AbstractSelectionTestCase.SQUARE_BRACKET_OPEN);
		int end= source.indexOf(AbstractSelectionTestCase.SQUARE_BRACKET_CLOSE);
		return new SourceRange(offset, end - offset);
	}

	private void check(ICompilationUnit cu, ISourceRange newRange) throws IOException, JavaModelException {
		String expected= getFileContents(getTestFileName(false));
		String actual= cu.getSource().substring(newRange.getOffset(), newRange.getOffset() + newRange.getLength());
//		assertEquals("selection incorrect length", expected.length(), actual.length());
		assertEquals("selection incorrect", expected, actual);
	}	
	
	private void helperSelectUp() throws Exception{
		ICompilationUnit cu= createCUfromTestFile(getPackageP(), true);
		ISourceRange selection= getSelection(cu);

		ISourceRange newRange= new StructureSelectEnclosingAction().getNewSelectionRange(selection, cu);
		
		check(cu, newRange);
	}
	
	private void helperSelectUp(int startLine, int startColumn, int endLine, int endColumn) throws Exception{
		ICompilationUnit cu= createCUfromTestFile(getPackageP(), true);
		ISourceRange selection= TextRangeUtil.getSelection(cu, startLine, startColumn, endLine, endColumn);
		ISourceRange newRange= new StructureSelectEnclosingAction().getNewSelectionRange(selection, cu);

		check(cu, newRange);
	}	
	
	private void helperSelectNext(int startLine, int startColumn, int endLine, int endColumn) throws Exception{
		ICompilationUnit cu= createCUfromTestFile(getPackageP(), true);
		ISourceRange selection= TextRangeUtil.getSelection(cu, startLine, startColumn, endLine, endColumn);

		ISourceRange newRange= new StructureSelectNextAction().getNewSelectionRange(selection, cu);
		check(cu, newRange);
	}	
	
	private void helperSelectPrevious(int startLine, int startColumn, int endLine, int endColumn) throws Exception{
		ICompilationUnit cu= createCUfromTestFile(getPackageP(), true);
		ISourceRange selection= TextRangeUtil.getSelection(cu, startLine, startColumn, endLine, endColumn);

		ISourceRange newRange= new StructureSelectPreviousAction().getNewSelectionRange(selection, cu);
		check(cu, newRange);
	}	
		
	private void helperZeroLength(int line, int column) throws Exception{
		ICompilationUnit cu= createCUfromTestFile(getPackageP(), true);
		ISourceRange selection= new SourceRange(TextRangeUtil.getOffset(cu, line, column), 1);
			
		//DebugUtils.dump(name() + ":<" + cu.getSource().substring(selection.getOffset()) + "/>");
		
		ISourceRange newRange= new StructureSelectEnclosingAction().getNewSelectionRange(selection, cu);
		check(cu, newRange);
	}
	
	private void offsetTest(int line, int column, int expected) throws Exception{
		String filePath= TEST_PATH_PREFIX + getRefactoringPath() + "OffsetTest.java";
		ICompilationUnit cu= createCU(getPackageP(), "OffsetTest.java", getFileContents(filePath));
		assertEquals("incorrect offset", expected, TextRangeUtil.getOffset(cu, line, column));
	}
	

	// ---- tests --- 
	
	public void test0() throws Exception{
		helperSelectUp(4, 9, 4, 13);
	}
	
	public void test1() throws Exception{
		helperSelectUp();
	}

	public void test2() throws Exception{
		helperSelectUp(4, 16, 4, 21);
	}

	public void test3() throws Exception{
		helperSelectUp(4, 9, 4, 21);
	}

	public void test4() throws Exception{
		helperSelectUp();
	}
	
	public void test5() throws Exception{
		helperSelectUp();
	}
	
	public void test6() throws Exception{
		helperSelectUp();
	}

	public void test7() throws Exception{
		//helper1();
		helperSelectUp(3, 10, 3, 14);
	}

	public void test8() throws Exception{
		helperSelectUp(3, 16, 3, 18);
	}

	public void test9() throws Exception{
		helperSelectUp(3, 10, 3, 11);
	}
	
	public void test10() throws Exception{
		helperSelectUp(4, 18, 4, 21);
	}

	public void test11() throws Exception{
		helperSelectUp(4, 20, 4, 21);
	}

	public void test12() throws Exception{
		helperSelectUp(4, 16, 4, 19);
	}
	
	public void test13() throws Exception{
		helperSelectUp(4, 13, 4, 16);
	}
	
	public void test14() throws Exception{
		helperSelectUp(4, 16, 4, 21);
	}
	
	public void test15() throws Exception{
		// identical to test9 ???
		helperSelectUp(3, 10, 3, 11);
	}
	
	public void test16() throws Exception{
		helperSelectUp(3, 16, 3, 17);
	}
	
	public void test17() throws Exception{
		helperSelectUp(3, 5, 7, 6);
	}
	
	public void test18() throws Exception{
		helperSelectUp(3, 5, 4, 6);
	}
	
	public void test19() throws Exception{
		helperSelectUp(7, 14, 7, 16);
	}
	
	public void test20() throws Exception{
		helperSelectUp(4, 18, 4, 19);
	}
	
	public void test21() throws Exception{
		//regression test for bug#10182
		//printTestDisabledMessage("regression test for bug#11151");
		helperSelectNext(3, 21, 3, 28);
	}
	
	public void test22() throws Exception{
		//regression test for bug#10182
		//printTestDisabledMessage("regression test for bug#11151");
		helperSelectPrevious(3, 21, 3, 28);
	}
	
	public void test23() throws Exception{
//		printTestDisabledMessage("regression test for bug#10570");
		helperSelectPrevious(5, 30, 7, 10);
	}
	
	public void test24() throws Exception{
		//regression test for bug#11424
		helperSelectPrevious(3, 13, 5, 6);
	}

	public void test25() throws Exception{
		//regression test for bug#11879
		helperSelectNext(5, 5, 6, 6);
	}
	
	public void test26() throws Exception{
		//regression test for bug#11879
		helperSelectPrevious(5, 5, 6, 6);
	}
	
	public void test27() throws Exception{
		//regression test for bug#11879
		helperSelectNext(4, 1, 4, 10);
	}

	public void test28() throws Exception{
		//regression test for bug#11879
		helperSelectPrevious(4, 1, 4, 10);
	}
	
	public void test29() throws Exception{
//		printTestDisabledMessage("regression test for bug#16051");
		helperSelectUp(5, 13, 5, 17);
	}

	public void test30() throws Exception{
//		printTestDisabledMessage("regression test for bug#22082");
		helperSelectUp(3, 10, 3, 10);
	}
	
	public void test31() throws Exception{
//		printTestDisabledMessage("regression test for bug#22082");
		helperSelectUp(3, 10, 3, 10);
	}

	public void test32() throws Exception{
//		printTestDisabledMessage("regression test for bug#22939");
		helperSelectUp(4, 18, 4, 18);
	}

	public void test33() throws Exception{
//		printTestDisabledMessage("regression test for bug#22939");
		helperSelectUp(5, 23, 5, 23);
	}

	public void test34() throws Exception{
//		printTestDisabledMessage("regression test for bug#23118");
		helperSelectUp(5, 14, 5, 14);
	}

	public void test35() throws Exception{
//		printTestDisabledMessage("regression test for bug#23118");
		helperSelectUp(5, 14, 5, 14);
	}

	public void test36() throws Exception{
//		printTestDisabledMessage("regression test for bug#23259");
		helperSelectUp(5, 14, 5, 14);
	}

	public void test37() throws Exception{
//		printTestDisabledMessage("regression test for bug#23259");
		helperSelectUp(7, 14, 7, 14);
	}

	public void test38() throws Exception{
//		printTestDisabledMessage("regression test for bug#23263");
		helperSelectPrevious(4, 5, 5, 16);
	}

	public void test39() throws Exception{
//		printTestDisabledMessage("regression test for bug#23464");
		helperSelectPrevious(6, 13, 6, 20);
	}

	public void test40() throws Exception{
//		printTestDisabledMessage("regression test for bug#23464 ");
		helperSelectPrevious(7, 13, 7, 20);
	}
	
	public void testZeroLength0() throws Exception{
		//printTestDisabledMessage("");
		helperZeroLength(4, 20);
	}
	
	public void testWholeCu() throws Exception{
		ICompilationUnit cu= createCUfromTestFile(getPackageP(), true);
		ISourceRange selection= cu.getSourceRange();

		ISourceRange newRange= new StructureSelectEnclosingAction().getNewSelectionRange(selection, cu);
		
		String expected= getFileContents(getTestFileName(false));
		String actual= cu.getSource().substring(newRange.getOffset(), newRange.getOffset() + newRange.getLength());
		assertEquals("selection incorrect length", expected.length(), actual.length());
		assertEquals("selection incorrect", expected, actual);
	}

	//--- offset calculation tests
	
	public void testOffset0() throws Exception{
		offsetTest(4, 20, 47);
	}	
	
	public void testOffset1() throws Exception{
		offsetTest(5, 9, 53);
	}	
	
	public void testOffset2() throws Exception{
		offsetTest(7, 13, 81);
	}	
	
	public void testTabCount0(){
		int t= TextRangeUtil.calculateTabCountInLine("\t\t1", 9);
		assertEquals(2, t);
	}
	
	public void testTabCount1(){
		int t= TextRangeUtil.calculateTabCountInLine("\t\tint i= 1 + 1;", 20);
		assertEquals(2, t);
	}
	
	public void testTabCount2(){
		int t= TextRangeUtil.calculateTabCountInLine("\t\t\treturn;", 13);
		assertEquals(3, t);
	}
	
	public void testTabCount3(){
		int t= TextRangeUtil.calculateTabCountInLine("\tvoid m(){m();", 18);
		assertEquals(1, t);
	}
	
}