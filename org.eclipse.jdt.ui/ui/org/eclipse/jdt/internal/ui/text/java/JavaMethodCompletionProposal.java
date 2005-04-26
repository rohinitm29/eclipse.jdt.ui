/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.text.java;

import org.eclipse.swt.events.VerifyEvent;

import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy;

import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.Signature;

import org.eclipse.jdt.ui.PreferenceConstants;

import org.eclipse.jdt.internal.ui.JavaPlugin;


public class JavaMethodCompletionProposal extends JavaCompletionProposal2 {
	/** Triggers for method proposals without parameters. Do not modify. */
	protected final static char[] METHOD_TRIGGERS= new char[] { ';', ',', '.', '\t', '[', ' ' };
	/** Triggers for method proposals. Do not modify. */
	protected final static char[] METHOD_WITH_ARGUMENTS_TRIGGERS= new char[] { '(', '-', ' ' };
	protected final ICompilationUnit fCompilationUnit;

	protected static class ExitPolicy implements IExitPolicy {
	
		final char fExitCharacter;
	
		public ExitPolicy(char exitCharacter) {
			fExitCharacter= exitCharacter;
		}
	
		/*
		 * @see org.eclipse.jdt.internal.ui.text.link.LinkedPositionUI.ExitPolicy#doExit(org.eclipse.jdt.internal.ui.text.link.LinkedPositionManager, org.eclipse.swt.events.VerifyEvent, int, int)
		 */
		public ExitFlags doExit(LinkedModeModel environment, VerifyEvent event, int offset, int length) {
	
			if (event.character == fExitCharacter) {
				if (environment.anyPositionContains(offset))
					return new ExitFlags(ILinkedModeListener.UPDATE_CARET, false);
				else
					return new ExitFlags(ILinkedModeListener.UPDATE_CARET, true);
			}
	
			switch (event.character) {
			case ';':
				return new ExitFlags(ILinkedModeListener.NONE, true);
	
			default:
				return null;
			}
		}
	
	}

	public JavaMethodCompletionProposal(CompletionProposal proposal, ICompilationUnit cu) {
		super(proposal);
		fCompilationUnit= cu;
	}

	public void apply(IDocument document, char trigger, int offset) {
		super.apply(document, trigger, offset);
		try {
			setUpLinkedMode(document, getReplacementString());
		} catch (BadLocationException e) {
			// ignore
		}
	}

	private void setUpLinkedMode(IDocument document, String string) throws BadLocationException {
		if (fTextViewer != null && string != null) {
			int index= string.indexOf("()"); //$NON-NLS-1$
			if (index != -1 && index + 1 == getCursorPosition()) {
				IPreferenceStore preferenceStore= JavaPlugin.getDefault().getPreferenceStore();
				if (preferenceStore.getBoolean(PreferenceConstants.EDITOR_CLOSE_BRACKETS)) {
					int newOffset= getReplacementOffset() + getCursorPosition();

					LinkedPositionGroup group= new LinkedPositionGroup();
					group.addPosition(new LinkedPosition(document, newOffset, 0, LinkedPositionGroup.NO_STOP));

					LinkedModeModel model= new LinkedModeModel();
					model.addGroup(group);
					model.forceInstall();

					LinkedModeUI ui= new EditorLinkedModeUI(model, fTextViewer);
					ui.setSimpleMode(true);
					ui.setExitPolicy(new JavaMethodCompletionProposal.ExitPolicy(')'));
					ui.setExitPosition(fTextViewer, newOffset + 1, 0, Integer.MAX_VALUE);
					ui.setCyclingMode(LinkedModeUI.CYCLE_NEVER);
					ui.enter();
				}
			}
		}
	}
	
	public CharSequence getPrefixCompletionText(IDocument document, int completionOffset) {
		String string= getReplacementString();
		int pos= string.indexOf('(');
		if (pos > 0)
			return string.subSequence(0, pos);
		else
			return string;
	}
	
	protected IContextInformation computeContextInformation() {
		return new ProposalContextInformation(fProposal);
	}
	
	protected char[] computeTriggerCharacters() {
		if (Signature.getParameterCount(fProposal.getSignature()) > 0)
			return METHOD_WITH_ARGUMENTS_TRIGGERS;
		return METHOD_TRIGGERS;
	}
	
	protected int computeCursorPosition() {
		if (getReplacementString().endsWith(")")) //$NON-NLS-1$
			return getReplacementString().length() - 1;
		return super.computeCursorPosition();
	}

	protected ProposalInfo computeProposalInfo() {
		if (fCompilationUnit != null) {
			IJavaProject project= fCompilationUnit.getJavaProject();
			if (project != null)
				return new MethodProposalInfo(project, fProposal);
		}
		return super.computeProposalInfo();
	}
}
