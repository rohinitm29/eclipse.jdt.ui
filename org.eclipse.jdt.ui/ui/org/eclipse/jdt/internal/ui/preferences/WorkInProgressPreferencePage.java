/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.preferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferencePage;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.help.WorkbenchHelp;

import org.eclipse.jdt.ui.PreferenceConstants;

import org.eclipse.jdt.internal.ui.JavaPlugin;

/**
 * Preference page for work in progress.
 */
public class 
WorkInProgressPreferencePage
	extends PreferencePage
	implements IWorkbenchPreferencePage {

	/** prefix for resources */
	private static final String PREFIX= "WorkInProgress."; //$NON-NLS-1$

	/** 
	 * All FieldEditors except <code>smartTyping</code>, whose enable state
	 * is controlled by the smartTyping preference.
	 */
	private Set fSmartTypingItems= new HashSet();
	
	private List fCheckBoxes;
	private List fRadioButtons;
	private List fTextControls;
	
	/** Color editor for choosing line number ruler colors. */
	private ColorEditor fColorEditor;
	/** List containing the colors for the diff line number ruler. */
	private org.eclipse.swt.widgets.List fColorList;
	/** Preferences keys and label text for the color list */
	final static String[][] fColorModels= new String[][] {
		{PreferencesMessages.getString(PREFIX + "editor.changedLineColor"), PreferenceConstants.LINE_NUMBER_CHANGED_COLOR}, //$NON-NLS-1$
		{PreferencesMessages.getString(PREFIX + "editor.addedLineColor"), PreferenceConstants.LINE_NUMBER_ADDED_COLOR}, //$NON-NLS-1$
		{PreferencesMessages.getString(PREFIX + "editor.deletedLineColor"), PreferenceConstants.LINE_NUMBER_DELETED_COLOR}, //$NON-NLS-1$
	};
	
	/**
	 * creates a new preference page.
	 */
	public WorkInProgressPreferencePage() {
		setPreferenceStore(getPreferenceStore());
		fRadioButtons= new ArrayList();
		fCheckBoxes= new ArrayList();
		fTextControls= new ArrayList();
	}

	private Button addCheckBox(Composite parent, String label, String key) { 
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		
		Button button= new Button(parent, SWT.CHECK);
		button.setText(label);
		button.setData(key);
		button.setLayoutData(gd);

		button.setSelection(getPreferenceStore().getBoolean(key));
		
		fCheckBoxes.add(button);
		return button;
	}
	
	/*
	 * @see PreferencePage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		WorkbenchHelp.setHelp(getControl(), "WORK_IN_PROGRESS_PREFERENCE_PAGE"); //$NON-NLS-1$
	}

	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite result= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth= 0;
		layout.verticalSpacing= convertVerticalDLUsToPixels(10);
		layout.horizontalSpacing= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		result.setLayout(layout);
		
		Group group= new Group(result, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText(PreferencesMessages.getString(PREFIX + "smartTyping")); //$NON-NLS-1$
		
		addCheckBox(group, PreferencesMessages.getString(PREFIX + "overwriteMode"), PreferenceConstants.EDITOR_DISABLE_OVERWRITE_MODE); //$NON-NLS-1$
		createSpacer(group, 1);

		Label label= new Label(group, SWT.NONE);
		label.setText(PreferencesMessages.getString(PREFIX + "smartTyping.label")); //$NON-NLS-1$

		Button button= addCheckBox(group, PreferencesMessages.getString(PREFIX + "smartTyping.smartSemicolon"), PreferenceConstants.EDITOR_SMART_SEMICOLON); //$NON-NLS-1$
		fSmartTypingItems.add(button);
		
		button= addCheckBox(group, PreferencesMessages.getString(PREFIX + "smartTyping.smartOpeningBrace"), PreferenceConstants.EDITOR_SMART_OPENING_BRACE); //$NON-NLS-1$
		fSmartTypingItems.add(button);
		
		/* line change bar */
		group= new Group(result, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText(PreferencesMessages.getString(PREFIX + "editor")); //$NON-NLS-1$
		
		button= addCheckBox(group, PreferencesMessages.getString(PREFIX + "editor.lineChangeRuler"), PreferenceConstants.LINE_NUMBER_RULER_QUICK_DIFF); //$NON-NLS-1$
		Label l= new Label(group, SWT.LEFT );
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan= 2;
		gd.heightHint= convertHeightInCharsToPixels(1) / 2;
		l.setLayoutData(gd);
	
		l= new Label(group, SWT.LEFT);
		l.setText(PreferencesMessages.getString(PREFIX + "editor.appearanceOptions")); //$NON-NLS-1$
		gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan= 2;
		l.setLayoutData(gd);
	
		Composite editorComposite= new Composite(group, SWT.NONE);
		layout= new GridLayout();
		layout.numColumns= 2;
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		editorComposite.setLayout(layout);
		gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.FILL_VERTICAL);
		gd.horizontalSpan= 2;
		editorComposite.setLayoutData(gd);		
	
		fColorList= new org.eclipse.swt.widgets.List(editorComposite, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		gd= new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
		gd.heightHint= convertHeightInCharsToPixels(3);
		fColorList.setLayoutData(gd);
				
		Composite stylesComposite= new Composite(editorComposite, SWT.NONE);
		layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		layout.numColumns= 2;
		stylesComposite.setLayout(layout);
		stylesComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
	
		l= new Label(stylesComposite, SWT.LEFT);
		l.setText(PreferencesMessages.getString("JavaEditorPreferencePage.color")); //$NON-NLS-1$
		gd= new GridData();
		gd.horizontalAlignment= GridData.BEGINNING;
		l.setLayoutData(gd);
	
		fColorEditor= new ColorEditor(stylesComposite);
		Button foregroundColorButton= fColorEditor.getButton();
		gd= new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment= GridData.BEGINNING;
		foregroundColorButton.setLayoutData(gd);
	
		for (int i= 0; i < fColorModels.length; i++)
			fColorList.add(fColorModels[i][0]);
		fColorList.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (fColorList != null && !fColorList.isDisposed()) {
					fColorList.select(0);
					handleListSelection();
				}
			}
		});
				
		fColorList.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
			public void widgetSelected(SelectionEvent e) {
				handleListSelection();
			}
		});
		foregroundColorButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
			public void widgetSelected(SelectionEvent e) {
				int i= fColorList.getSelectionIndex();
				String key= fColorModels[i][1];
		
				PreferenceConverter.setValue(getPreferenceStore(), key, fColorEditor.getColorValue());
			}
		});
		
		group= new Group(result, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText(PreferencesMessages.getString(PREFIX + "refactoring")); //$NON-NLS-1$
		
		button= addCheckBox(group, PreferencesMessages.getString(PREFIX + "refactoring.participants"), "org.eclipse.jdt.refactoring.participants"); //$NON-NLS-1$ //$NON-NLS-2$

		group= new Group(result, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText(PreferencesMessages.getString(PREFIX + "quickassist.group")); //$NON-NLS-1$
		
		button= addCheckBox(group, PreferencesMessages.getString(PREFIX + "quickassist.option"), PreferenceConstants.APPEARANCE_QUICKASSIST_LIGHTBULB); //$NON-NLS-1$ //$NON-NLS-2$

		return result;
	}
	
 
	/**
	 * Handles selection in the color list for the diff line number ruler.
	 */
	void handleListSelection() {
		int i= fColorList.getSelectionIndex();
		String key= fColorModels[i][1];
		RGB rgb= PreferenceConverter.getColor(getPreferenceStore(), key);
		fColorEditor.setColorValue(rgb);
	}

	/*
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	protected void createSpacer(Composite composite, int columnSpan) {
		Label label= new Label(composite, SWT.NONE);
		GridData gd= new GridData();
		gd.horizontalSpan= columnSpan;
		label.setLayoutData(gd);
	}

	/*
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	protected IPreferenceStore doGetPreferenceStore() {
		return JavaPlugin.getDefault().getPreferenceStore();
	}

	/*
	 * @see PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		IPreferenceStore store= getPreferenceStore();
		for (int i= 0; i < fCheckBoxes.size(); i++) {
			Button button= (Button) fCheckBoxes.get(i);
			String key= (String) button.getData();
			button.setSelection(store.getDefaultBoolean(key));
		}
		for (int i= 0; i < fRadioButtons.size(); i++) {
			Button button= (Button) fRadioButtons.get(i);
			String[] info= (String[]) button.getData();
			button.setSelection(info[1].equals(store.getDefaultString(info[0])));
		}
		for (int i= 0; i < fTextControls.size(); i++) {
			Text text= (Text) fTextControls.get(i);
			String key= (String) text.getData();
			text.setText(store.getDefaultString(key));
		}
		// color table
		store.setToDefault(PreferenceConstants.LINE_NUMBER_CHANGED_COLOR);
		store.setToDefault(PreferenceConstants.LINE_NUMBER_ADDED_COLOR);
		store.setToDefault(PreferenceConstants.LINE_NUMBER_DELETED_COLOR);
		handleListSelection();
		
		super.performDefaults();
	}

	/*
	 * @see IPreferencePage#performOk()
	 */
	public boolean performOk() {
		IPreferenceStore store= getPreferenceStore();
		for (int i= 0; i < fCheckBoxes.size(); i++) {
			Button button= (Button) fCheckBoxes.get(i);
			String key= (String) button.getData();
			store.setValue(key, button.getSelection());
		}
		for (int i= 0; i < fRadioButtons.size(); i++) {
			Button button= (Button) fRadioButtons.get(i);
			if (button.getSelection()) {
				String[] info= (String[]) button.getData();
				store.setValue(info[0], info[1]);
			}
		}
		for (int i= 0; i < fTextControls.size(); i++) {
			Text text= (Text) fTextControls.get(i);
			String key= (String) text.getData();
			store.setValue(key, text.getText());
		}
		
		JavaPlugin.getDefault().savePluginPreferences();
		return super.performOk();
	}

	/**
	 * @param store
	 */
	public static void initDefaults(IPreferenceStore store) {
		store.setDefault(PreferenceConstants.EDITOR_DISABLE_OVERWRITE_MODE, false);
		store.setDefault(PreferenceConstants.EDITOR_SMART_SEMICOLON, false);
		store.setDefault(PreferenceConstants.EDITOR_SMART_OPENING_BRACE, false);
		
		store.setDefault(PreferenceConstants.APPEARANCE_QUICKASSIST_LIGHTBULB, true); //$NON-NLS-1$
	}
}
