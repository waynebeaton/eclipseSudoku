/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.sudoku.ecf.presence.ui;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.ui.roster.AbstractRosterEntryContributionItem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.sudoku.ecf.Activator;

public class SudokuShareRosterEntryContributionItem extends
		AbstractRosterEntryContributionItem {

	public SudokuShareRosterEntryContributionItem() {
	}

	public SudokuShareRosterEntryContributionItem(String id) {
		super(id);
	}

	protected IAction[] makeActions() {
		// Else check for Roster entry
		final IRosterEntry entry = getSelectedRosterEntry();
		IContainer c = getContainerForRosterEntry(entry);
		// If roster entry is selected and it has a container
		if (entry != null && c != null) {
			final IChannelContainerAdapter channelAdapter = (IChannelContainerAdapter) c
					.getAdapter(IChannelContainerAdapter.class);
			// If the container has channel container adapter and is
			// online/available
			if (channelAdapter != null && isAvailable(entry)) {
				SudokuShare tmp = SudokuShareRosterContributionItem
						.getSudokuShare(c.getID());
				// If there is an URL share associated with this container
				if (tmp != null) {
					final SudokuShare sudokushare = tmp;
					IAction action = new Action() {
						public void run() {
							sudokushare.startPlaying(entry.getUser().getID());
						}
					};
					action.setText("Start Sudoku Sharing");
					action.setImageDescriptor(Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/sudokuMain.gif"));
					return new IAction[] { action };
				}
			}
		}
		return null;
	}

}
