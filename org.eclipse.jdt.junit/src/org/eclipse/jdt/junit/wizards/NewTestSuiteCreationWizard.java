/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
package org.eclipse.jdt.junit.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.junit.ui.JUnitPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IEditorPart;

/**
 * A wizard for creating test suites.
 */
public class NewTestSuiteCreationWizard extends JUnitWizard {

	private NewTestSuiteCreationWizardPage fPage;
	
	public NewTestSuiteCreationWizard() {
		super();
		//setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWCLASS);
		//setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
		setWindowTitle(Messages.getString("Wizard.title.new")); //$NON-NLS-1$
		initDialogSettings();
	}

	/*
	 * @see Wizard#createPages
	 */	
	public void addPages() {
		super.addPages();
		//IWorkspace workspace= JavaPlugin.getWorkspace();
		IWorkspace workspace= ResourcesPlugin.getWorkspace();
		fPage= new NewTestSuiteCreationWizardPage();
		addPage(fPage);
		fPage.init(getSelection());
	}	

	/*
	 * @see Wizard#performFinish
	 */		
	public boolean performFinish() {
		IPackageFragment pack= fPage.getPackageFragment();
		String filename= fPage.getTypeName() + ".java"; //$NON-NLS-1$
		ICompilationUnit cu= pack.getCompilationUnit(filename);
		if (cu.exists()) {
			IEditorPart cu_ep= EditorUtility.isOpenInEditor(cu);
			if (cu_ep != null && cu_ep.isDirty()) {
				boolean saveUnsavedChanges= MessageDialog.openQuestion(fPage.getShell(), Messages.getString("NewTestSuiteWiz.unsavedchangesDialog.title"), Messages.getFormattedString("NewTestSuiteWiz.unsavedchangesDialog.message", filename)); //$NON-NLS-1$ //$NON-NLS-2$
				if (saveUnsavedChanges) {
					ProgressMonitorDialog progressDialog= new ProgressMonitorDialog(fPage.getShell());
					try {
						progressDialog.run(false, false, getRunnableSave(cu_ep));
					} catch (Exception e) {
						JUnitPlugin.log(e);
					}
				}
			}
			IType suiteType= cu.getType(fPage.getTypeName());
			IMethod suiteMethod= suiteType.getMethod("suite", new String[] {}); //$NON-NLS-1$
			if (suiteMethod.exists()) {
				try {
				ISourceRange range= suiteMethod.getSourceRange();
				IBuffer buf= cu.getBuffer();
				String originalContent= buf.getText(range.getOffset(), range.getLength());
				int start= originalContent.indexOf(NewTestSuiteCreationWizardPage.START_MARKER);
				if (start > -1) {
					int end= originalContent.indexOf(NewTestSuiteCreationWizardPage.END_MARKER, start);
					if (end < 0) {
						fPage.cannotUpdateSuiteError();
						return false;
					}
				} else {
					fPage.cannotUpdateSuiteError();
					return false;
				}
				} catch (JavaModelException e) {
					JUnitPlugin.log(e);
					return false;
				}
			}
		}
		
		if (finishPage(fPage.getRunnable())) {
			if (!fPage.hasUpdatedExistingClass())
				postCreatingType();
			fPage.saveWidgetValues();				
			return true;
		}

		return false;		
	}

	protected void postCreatingType() {
		IType newClass= fPage.getCreatedType();
		
		ICompilationUnit cu= newClass.getCompilationUnit();
		if (cu.isWorkingCopy()) {
			cu= (ICompilationUnit) cu.getOriginalElement();
			//added here
		}
		try {
			IResource resource= cu.getUnderlyingResource();
			selectAndReveal(resource);
			openResource(resource);
		} catch (JavaModelException e) {
			JUnitPlugin.log(e);
		}
	}

	public NewTestSuiteCreationWizardPage getPage() {
		return fPage;
	}
	
	public IRunnableWithProgress getRunnableSave(final IEditorPart cu_ep) {
		return new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					if (monitor == null) {
						monitor= new NullProgressMonitor();
					}
					cu_ep.doSave(monitor);
			}
		};
	}

}
